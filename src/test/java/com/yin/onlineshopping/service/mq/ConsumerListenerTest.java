package com.yin.onlineshopping.service.mq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Date;

@SpringBootTest
class ConsumerListenerTest {
    @Resource
    RocketMQService rocketMQService;
    @Test
    void onMessage() throws Exception {
        rocketMQService.sendMessage("consumerTopic", "Today is " + new Date());
    }
}