package com.yin.onlineshopping.service;

import com.alibaba.fastjson.JSON;
import com.yin.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.yin.onlineshopping.db.dao.OnlineShoppingOrderDao;
import com.yin.onlineshopping.db.po.OnlineShoppingCommodity;
import com.yin.onlineshopping.db.po.OnlineShoppingOrder;
import com.yin.onlineshopping.service.mq.RocketMQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    @Resource
    OnlineShoppingOrderDao orderDao;

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    RedisService redisService;

    @Resource
    RocketMQService rocketMQService;

    public OnlineShoppingOrder processOrderOriginal(long userId, long commodityId) {
        OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(commodityId);
        int availableStock = commodity.getAvailableStock();
        if (availableStock > 0) {
            availableStock--;

            log.info("Process Order success for CommodityId:" + commodity.getCommodityId());
            commodity.setAvailableStock(availableStock);
            commodityDao.updateCommodity(commodity);
            OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                    .commodityId(commodityId)
                    .userId(userId)
                    .orderNo(UUID.randomUUID().toString())
                    // create order
                    // 0. Invalid order, Since no available stock
                    // 1. already create order, pending for payment
                    // 2. finished payment
                    // 99. invalid order due to payment proceed overtime
                    .orderStatus(1)
                    .createTime(new Date())
                    .orderAmount((long)commodity.getPrice())
                    .build();
            orderDao.insertOrder(order);
            return order;
        }
        return null;
    }

    public OnlineShoppingOrder processOrderInOneSql(long userId, long commodityId) {
        OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(commodityId);
        int availableStock = commodity.getAvailableStock();
        if (availableStock > 0) {
            log.info("Process Order success for CommodityId:" + commodity.getCommodityId());

            int result = commodityDao.deductStock(commodity.getCommodityId());
            if (result > 0) {
                OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                        .commodityId(commodityId)
                        .userId(userId)
                        .orderNo(UUID.randomUUID().toString())
                        // create order
                        // 0. Invalid order, Since no available stock
                        // 1. already create order, pending for payment
                        // 2. finished payment
                        // 99. invalid order due to payment proceed overtime
                        .orderStatus(1)
                        .createTime(new Date())
                        .orderAmount((long) commodity.getPrice())
                        .build();
                orderDao.insertOrder(order);
                return order;
            }
        }
        return null;
    }

    public OnlineShoppingOrder processOrderRedis(long userId, long commodityId) throws Exception {
        String redisKey = "commodity:" + commodityId;
        long availableStock = redisService.stockDeduct(redisKey);
        if( availableStock >= 0) {
            OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                    .commodityId(commodityId)
                    .userId(userId)
                    .orderNo(UUID.randomUUID().toString())
                    // create order
                    // 0. Invalid order, Since no available stock
                    // 1. already create order, pending for payment
                    // 2. finished payment
                    // 99. invalid order due to payment proceed overtime
                    .build();
            String msg = JSON.toJSONString(order);
            rocketMQService.sendMessage("createOrder", msg);
            return order;
        }
        return null;
    }


    public OnlineShoppingOrder processOrderWithDistributedLock(long userId, long commodityId) {
        String redisKey = "lock_commodity:" + commodityId;
        String requestId = UUID.randomUUID().toString();
        boolean result = redisService.tryGetDistributedLock(redisKey, requestId, 10000);
        if (result) {
            log.info("Get Distributed Lock for Commodity:" + commodityId);
            OnlineShoppingOrder onlineShoppingOrder = processOrderOriginal(userId, commodityId);
            redisService.releaseDistributedLock(redisKey, requestId);
            return onlineShoppingOrder;
        }

        log.info("Please try again for commodityId:" + commodityId);
        return null;
    }


    public OnlineShoppingOrder processOrderWithSp(long userId, long commodityId) {
        OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(commodityId);
        int availableStock = commodity.getAvailableStock();
        if (availableStock > 0) {
            log.info("Process Order success for CommodityId:" + commodity.getCommodityId());

            int result = commodityDao.deductStockWithSP(commodity.getCommodityId());
            if (result > 0) {
                OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                        .commodityId(commodityId)
                        .userId(userId)
                        .orderNo(UUID.randomUUID().toString())
                        // create order
                        // 0. Invalid order, Since no available stock
                        // 1. already create order, pending for payment
                        // 2. finished payment
                        // 99. invalid order due to payment proceed overtime
                        .orderStatus(1)
                        .createTime(new Date())
                        .orderAmount((long) commodity.getPrice())
                        .build();
                orderDao.insertOrder(order);
                return order;
            }
        }
        return null;
    }
    public OnlineShoppingOrder getOrderByOrderNo(String orderNo) {
        return orderDao.queryOrderByOrderNo(orderNo);
    }

    public void payOrder(String orderNumber) {
        OnlineShoppingOrder order = getOrderByOrderNo(orderNumber);
        // create order
        // 0. Invalid order, Since no available stock
        // 1. already create order, pending for payment
        // 2. finished payment
        // 99. invalid order due to payment proceed overtime
        order.setPayTime(new Date());
         order.setOrderStatus(2);
        // save order instance into DB
        orderDao.updateOrder(order);
    }
}
