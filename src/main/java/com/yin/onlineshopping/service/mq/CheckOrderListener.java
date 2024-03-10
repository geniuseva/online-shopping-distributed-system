package com.yin.onlineshopping.service.mq;

import com.alibaba.fastjson.JSON;
import com.yin.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.yin.onlineshopping.db.dao.OnlineShoppingOrderDao;
import com.yin.onlineshopping.db.po.OnlineShoppingOrder;
import com.yin.onlineshopping.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

 @Component
@Slf4j
@RocketMQMessageListener(topic = "checkOrder", consumerGroup = "checkOrderGroup")
public class CheckOrderListener implements RocketMQListener<MessageExt> {

     @Resource
     OnlineShoppingOrderDao orderDao;

     @Resource
     OnlineShoppingCommodityDao commodityDao;

     @Resource
     RedisService redisService;
    @Override
    public void onMessage(MessageExt messageExt) {
        String body = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("Receive message form checkOrder, content:" + body);
        OnlineShoppingOrder orderMsg = JSON.parseObject(body, OnlineShoppingOrder.class);
        OnlineShoppingOrder orderDb = orderDao.queryOrderByOrderNo(orderMsg.getOrderNo());

        if (orderDb == null) {
            log.error("Can't find order {}", orderMsg.getOrderNo());
            return;
        }

        // 0: invalid order
        // 1. pending payment
        // 2. finish payment
        // 99. overtime order
        if (orderDb.getOrderStatus()!= 2) {
            //set it to invalid
            orderDb.setOrderStatus(99);
            orderDao.updateOrder(orderDb);
            // revert stock
            commodityDao.revertStock(orderDb.getCommodityId());
            String key = "commodity:" + orderDb.getCommodityId();
            redisService.revertStock(key);
            redisService.removeFromDenyList(orderDb.getUserId(), orderDb.getCommodityId());
            log.info("Revert order since it passed max payment time, order: {}", orderDb.getOrderNo());
        } else {
            log.info("Skip operation for order {} since it is finished", orderDb.getOrderNo());
        }
    }
}