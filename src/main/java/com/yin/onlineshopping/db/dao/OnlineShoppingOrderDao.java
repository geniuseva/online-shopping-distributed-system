package com.yin.onlineshopping.db.dao;

import com.yin.onlineshopping.db.po.OnlineShoppingOrder;

public interface OnlineShoppingOrderDao {
    int insertOrder(OnlineShoppingOrder order);

    OnlineShoppingOrder queryOrderById(Long orderId);
    OnlineShoppingOrder queryOrderByOrderNo(String orderNo);

    int updateOrder(OnlineShoppingOrder order);
}
