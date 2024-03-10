package com.yin.onlineshopping.db.dao.impl;

import com.yin.onlineshopping.db.dao.OnlineShoppingUserDao;
import com.yin.onlineshopping.db.mappers.OnlineShoppingUserMapper;
import com.yin.onlineshopping.db.po.OnlineShoppingUser;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class OnlineShoppingUserDaoImpl implements OnlineShoppingUserDao {
    @Resource
    OnlineShoppingUserMapper mapper;

    @Override
    public OnlineShoppingUser queryUserById(Long userId) {
        return mapper.selectByPrimaryKey(userId);
    }
}
