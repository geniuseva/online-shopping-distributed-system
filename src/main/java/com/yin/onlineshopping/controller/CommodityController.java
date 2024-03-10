package com.yin.onlineshopping.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.yin.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.yin.onlineshopping.db.po.OnlineShoppingCommodity;
import com.yin.onlineshopping.service.EsService;
import com.yin.onlineshopping.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
@Slf4j
public class CommodityController {
    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    SearchService searchService;

    @Resource
    EsService esService;

    @RequestMapping("/addItem")
    public String addCommodity() {
        return "add_commodity";
    }

    @RequestMapping("/staticItem/{commodityId}")
    public String staticItemPage(
            @PathVariable("commodityId") long commodityId
    ) {

        return "item_detail_" + commodityId;
    }

    @PostMapping("/addItemAction")
    public String addCommodityAction(
            @RequestParam("commodityId") long commodityId,
            @RequestParam("commodityName") String commodityName,
            @RequestParam("commodityDesc") String commodityDesc,
            @RequestParam("price") int price,
            @RequestParam("availableStock") int availableStock,
            @RequestParam("creatorUserId") long creatorUserId,
            Map<String, Object> resultMap) {
        // create instanceof Commodity;
        OnlineShoppingCommodity commodity = OnlineShoppingCommodity.builder()
                .commodityId(commodityId)
                .commodityName(commodityName)
                .commodityDesc(commodityDesc)
                .price(price)
                .availableStock(availableStock)
                .lockStock(0)
                .totalStock(availableStock)
                .creatorUserId(creatorUserId)
                .build();
        // insert into DB
        onlineShoppingCommodityDao.insertCommodity(commodity);
        esService.addCommodityToEs(commodity);
        resultMap.put("Item",commodity);
        return "add_commodity_success";
    }

    @GetMapping("/listItems/{sellerId}")
    public String listItems(@PathVariable("sellerId") String sellerId,
                            Map<String, Object> resultMap) {
        try(Entry entry = SphU.entry("listItemsRule", EntryType.IN, 1, sellerId)) {
            List<OnlineShoppingCommodity> onlineShoppingCommodities =
                    onlineShoppingCommodityDao.listCommoditiesByUserId(Long.parseLong(sellerId));
            resultMap.put("itemList", onlineShoppingCommodities);
            return "list_items";
        } catch (BlockException e) {
           log.error("ListItems got throttled, error: {}", e.getMessage());
           return "wait";
        }
    }

    @GetMapping("/item/{commodityId}")
    public String getItem(@PathVariable("commodityId") long commodityId,
                          Map<String, Object> resultMap) {
        OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(commodityId);
        resultMap.put("commodity", commodity);
        return "item_detail";
    }
    @RequestMapping("/searchAction")
    public String search(@RequestParam("keyWord") String keyword,
                         Map<String, Object> resultMap) {
        List<OnlineShoppingCommodity> onlineShoppingCommodities = searchService.searchCommodityByEs(keyword);
        resultMap.put("itemList", onlineShoppingCommodities);
        return "search_items";
    }
    @PostConstruct
    public void commodityControlFlow() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("listItemsRule");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(1);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
