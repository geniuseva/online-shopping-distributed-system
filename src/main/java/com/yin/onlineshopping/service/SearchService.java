package com.yin.onlineshopping.service;

import com.yin.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.yin.onlineshopping.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class SearchService {
    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    EsService esService;

    public List<OnlineShoppingCommodity> searchCommodityBySQL(String keyword) {
        return commodityDao.queryCommodityByKeyword(keyword);
    }

    public List<OnlineShoppingCommodity> searchCommodityByEs(String keyword) {
        return esService.searchCommodities(keyword, 0 , 20);
    }

}
