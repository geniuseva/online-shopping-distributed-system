package com.yin.onlineshopping.db.dao.impl;

import com.yin.onlineshopping.db.dao.OnlineShoppingOrderDao;
import com.yin.onlineshopping.db.mappers.OnlineShoppingOrderMapper;
import com.yin.onlineshopping.db.po.OnlineShoppingOrder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class OnlineShoppingOrderDaoImpl implements OnlineShoppingOrderDao {
    @Resource
    OnlineShoppingOrderMapper mapper;
    @Override
    public int insertOrder(OnlineShoppingOrder order) {
        return mapper.insert(order);
    }

    @Override
    public OnlineShoppingOrder queryOrderById(Long orderId) {
        return mapper.selectByPrimaryKey(orderId);
    }

    @Override
    public OnlineShoppingOrder queryOrderByOrderNo(String orderNo) {
        return mapper.queryOrderByOrderNo(orderNo);
    }

    @Override
    public int updateOrder(OnlineShoppingOrder order) {
        return mapper.updateByPrimaryKey(order);
    }
}
