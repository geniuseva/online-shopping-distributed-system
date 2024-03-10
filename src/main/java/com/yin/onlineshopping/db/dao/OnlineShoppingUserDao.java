package com.yin.onlineshopping.db.dao;

import com.yin.onlineshopping.db.po.OnlineShoppingUser;

public interface OnlineShoppingUserDao {
    OnlineShoppingUser queryUserById(Long userId);
}
