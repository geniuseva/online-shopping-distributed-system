package com.yin.onlineshopping.controller;

import org.elasticsearch.common.inject.Inject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
public class HelloController {
    @Resource
    DependencyA dependencyA;
    @Inject
    public HelloController(DependencyA dependencyA) {
        this.dependencyA = dependencyA;
    }
    @GetMapping("/")
    public String helloWorld() {
        return dependencyA.send("Hello world");
    }

    @GetMapping("/staticSend")
    public String staticHello() {
        return StaticDependency.staticSend("Hello world!!!");
    }
    @GetMapping("/echo/{text}")
    public String echo(@PathVariable("text") String text) {
        return  "You just Input :" + text;
    }
}
