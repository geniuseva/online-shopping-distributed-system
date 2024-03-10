package com.yin.onlineshopping.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class CdnPageServiceTest {
    @Resource
    CdnPageService cdnPageService;
    @Test
    void createHtml() {
        cdnPageService.createHtml(999);
    }
}