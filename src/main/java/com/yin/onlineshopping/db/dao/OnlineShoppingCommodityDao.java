package com.yin.onlineshopping.db.dao;

import com.yin.onlineshopping.db.po.OnlineShoppingCommodity;

import java.util.List;

public interface OnlineShoppingCommodityDao {
    int insertCommodity(OnlineShoppingCommodity record);

    int deleteCommodity(Long commodityId);
    int updateCommodity(OnlineShoppingCommodity record);
    List<OnlineShoppingCommodity> listCommoditiesByUserId(long userId);
    List<OnlineShoppingCommodity> listCommodities();
    OnlineShoppingCommodity queryCommodityById(Long commodityId);

    int deductStock(Long commodityId);

    int deductStockWithSP(Long commodityId);

    int revertStock(Long commodityId);

    List<OnlineShoppingCommodity> queryCommodityByKeyword(String keyword);
}
