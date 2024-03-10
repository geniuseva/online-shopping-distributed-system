package com.yin.onlineshopping.service.mq;

import com.alibaba.fastjson.JSON;
import com.yin.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.yin.onlineshopping.db.dao.OnlineShoppingOrderDao;
import com.yin.onlineshopping.db.po.OnlineShoppingCommodity;
import com.yin.onlineshopping.db.po.OnlineShoppingOrder;
import com.yin.onlineshopping.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
@RocketMQMessageListener(topic = "createOrder", consumerGroup = "createOrderGroup")
public class CreateOrderListener implements RocketMQListener<MessageExt> {

    @Resource
    OnlineShoppingCommodityDao commodityDao;
    @Resource
    OnlineShoppingOrderDao orderDao;
    @Resource
    RedisService redisService;

    @Resource
    RocketMQService rocketMQService;
    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("createOrder Message body:" + message);
        OnlineShoppingOrder order = JSON.parseObject(message, OnlineShoppingOrder.class);

        int res = commodityDao.deductStock(order.getCommodityId());
        if (res > 0) {
            OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(order.getCommodityId());
            order.setOrderStatus(1);
            order.setCreateTime(new Date());
            order.setOrderAmount(commodity.getPrice().longValue());
            orderDao.insertOrder(order);
            String redisKey = "commodity:" + commodity.getCommodityId();
            redisService.setValue(redisKey , commodity.getAvailableStock().toString());
            try {
                rocketMQService.sendDelayMessage("checkOrder", JSON.toJSONString(order),3);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

    }
}