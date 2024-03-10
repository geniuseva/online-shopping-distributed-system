package com.yin.onlineshopping.db.dao.impl;

import com.yin.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.yin.onlineshopping.db.mappers.OnlineShoppingCommodityMapper;
import com.yin.onlineshopping.db.po.OnlineShoppingCommodity;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OnlineShoppingCommodityDaoImpl implements OnlineShoppingCommodityDao {

    @Resource
    OnlineShoppingCommodityMapper mapper;

    @Override
    public int insertCommodity(OnlineShoppingCommodity record) {
        return mapper.insert(record);
    }

    @Override
    public int deleteCommodity(Long commodityId) {
        return mapper.deleteByPrimaryKey(commodityId);
    }

    @Override
    public int updateCommodity(OnlineShoppingCommodity record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public List<OnlineShoppingCommodity> listCommoditiesByUserId(long userId) {
                return mapper.listCommoditiesByUserId(userId);
    }

    @Override
    public List<OnlineShoppingCommodity> listCommodities() {
        return mapper.listCommodities();
    }

    @Override
    public OnlineShoppingCommodity queryCommodityById(Long commodityId) {
        return mapper.selectByPrimaryKey(commodityId);
    }

    @Override
    public int deductStock(Long commodityId) {
        return mapper.deductStock(commodityId);
    }

    @Override
    public int deductStockWithSP(Long commodityId) {
        Map<String, Object> para = new HashMap<>();
        para.put("commodityId", commodityId);
        para.put("res ", 0);
        mapper.deductStockWithSp(para);
        Object res = para.getOrDefault("res", 0);
        return (int) res;
    }

    @Override
    public int revertStock(Long commodityId) {
        return mapper.revertStock(commodityId);
    }

    @Override
    public List<OnlineShoppingCommodity> queryCommodityByKeyword(String keyword) {
        String keywordPattern = "%" + keyword + "%";
        return mapper.queryCommodityByKeyword(keywordPattern);
    }
}
