package com.yin.onlineshopping.db.dao;

import com.yin.onlineshopping.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

@SpringBootTest
@Slf4j
class OnlineShoppingCommodityDaoTest {
    @Resource
    OnlineShoppingCommodityDao dao;

    @Test
    void insertCommodity() {
        dao.deleteCommodity(3L);
        OnlineShoppingCommodity commodity = OnlineShoppingCommodity.builder()
                .commodityId(3L)
                .commodityName("IPHONE Test 123")
                .commodityDesc("Iphone 13")
                .availableStock(10)
                .creatorUserId(123L)
                .totalStock(10)
                .lockStock(0)
                .price(999)
                .build();

        dao.insertCommodity(commodity);

        OnlineShoppingCommodity res = dao.queryCommodityById(3L);
        log.info("OnlineShoppingCommodity:" + res);
//        dao.deleteCommodity(3L);
    }

    @Test
    void listCommoditiesByUserId() {
        List<OnlineShoppingCommodity> onlineShoppingCommodities = dao.listCommoditiesByUserId(123);
        log.info(onlineShoppingCommodities.size() + "");
    }


    @Test
    void queryCommodityById() {
    }
}