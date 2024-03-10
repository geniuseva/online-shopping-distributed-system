package com.yin.onlineshopping.controller;

import org.springframework.stereotype.Service;

@Service
public class StaticDependency {
    
    public static String Lyon = "lyon1";
    public static String staticSend(String body) {
        //comment
	    return body;
    }
}
