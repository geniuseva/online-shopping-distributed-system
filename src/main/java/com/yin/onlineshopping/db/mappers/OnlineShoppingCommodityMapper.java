package com.yin.onlineshopping.db.mappers;

import com.yin.onlineshopping.db.po.OnlineShoppingCommodity;

import java.util.List;
import java.util.Map;

public interface OnlineShoppingCommodityMapper {
    int deleteByPrimaryKey(Long commodityId);

    int insert(OnlineShoppingCommodity record);

    int insertSelective(OnlineShoppingCommodity record);

    OnlineShoppingCommodity selectByPrimaryKey(Long commodityId);

    int updateByPrimaryKeySelective(OnlineShoppingCommodity record);

    int updateByPrimaryKey(OnlineShoppingCommodity record);

    List<OnlineShoppingCommodity> listCommoditiesByUserId(long userId);

    List<OnlineShoppingCommodity> listCommodities();

    int deductStock(long commodityId);

    void deductStockWithSp(Map<String, Object> para);

    int revertStock(long commodityId);

    List<OnlineShoppingCommodity> queryCommodityByKeyword(String keyword);
}