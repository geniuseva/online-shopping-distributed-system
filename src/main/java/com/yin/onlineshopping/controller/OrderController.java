package com.yin.onlineshopping.controller;

import com.yin.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.yin.onlineshopping.db.po.OnlineShoppingCommodity;
import com.yin.onlineshopping.db.po.OnlineShoppingOrder;
import com.yin.onlineshopping.service.OrderService;
import com.yin.onlineshopping.service.RedisService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class OrderController {
    @Resource
    OrderService orderService;

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    RedisService redisService;

    @RequestMapping("/commodity/buy/{userId}/{commodityId}")
    public String buyCommodity(
            @PathVariable("userId") long userId,
            @PathVariable("commodityId") long commodityId,
            Map<String, Object> resultMap) throws Exception {

        if (redisService.isInDenyList(userId, commodityId)) {
            resultMap.put("resultInfo", "Each user have only one quote for this commodity");
            return "order_result";
        }

        // OnlineShoppingOrder onlineShoppingOrder = orderService.processOrderOriginal(userId, commodityId);
        // OnlineShoppingOrder onlineShoppingOrder = orderService.processOrderInOneSql(userId, commodityId);
        // OnlineShoppingOrder onlineShoppingOrder = orderService.processOrderWithSp(userId, commodityId);
        OnlineShoppingOrder onlineShoppingOrder = orderService.processOrderRedis(userId, commodityId);
        // OnlineShoppingOrder onlineShoppingOrder = orderService.processOrderWithDistributedLock(userId, commodityId);
        if (onlineShoppingOrder !=null) {
            redisService.addToDenyList(userId, commodityId);
            resultMap.put("resultInfo", "Order create succesfully! Order Number:" + onlineShoppingOrder.getOrderNo());
            resultMap.put("orderNo", onlineShoppingOrder.getOrderNo());
        } else {
            resultMap.put("resultInfo", "The Commodity is out of stock");
        }
        return "order_result";
    }

    @RequestMapping("/commodity/orderQuery/{orderNo}")
    public ModelAndView orderQuery(@PathVariable String orderNo) {
        OnlineShoppingOrder order = orderService.getOrderByOrderNo(orderNo);
        OnlineShoppingCommodity commodity = commodityDao.queryCommodityById(order.getCommodityId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("order_check");
        modelAndView.addObject("order", order);
        modelAndView.addObject("commodity", commodity);
        return modelAndView;
    }
    @RequestMapping("commodity/payOrder/{orderNumber}")
    public String payOrder(@PathVariable("orderNumber")String orderNumber) {
        //  query  order from DB
        orderService.payOrder(orderNumber);
        return "redirect:/commodity/orderQuery/" + orderNumber;
    }

}
