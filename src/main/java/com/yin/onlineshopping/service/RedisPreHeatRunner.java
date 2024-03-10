package com.yin.onlineshopping.service;

import com.yin.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.yin.onlineshopping.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class RedisPreHeatRunner implements ApplicationRunner {
    @Resource
    OnlineShoppingCommodityDao commodityDao;
    @Resource
    RedisService redisService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO: read from mysql
        List<OnlineShoppingCommodity> onlineShoppingCommodities = commodityDao.listCommodities();
        for (OnlineShoppingCommodity commodity: onlineShoppingCommodities) {
            String redisKey = "commodity:" + commodity.getCommodityId();
            redisService.setValue(redisKey, (long)commodity.getAvailableStock());
            log.info("PreHeat starting, initialize commodity:" + commodity.getCommodityId());
        }
    }
}
