package com.yin.onlineshopping.controller;


import com.yin.onlineshopping.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    Map<String, User> users = new HashMap<>();

    @Resource(name = "lisi")
    User nobodyUser;
    @PostMapping("/users")
    public String createUser(@RequestParam("name") String name,
                             @RequestParam("email") String email,
                             Map<String, Object> resultMap) {
        User user = new User(name, email);
        users.put(name, user);
        resultMap.put("user", user);
        return "user_detail";
    }

    @GetMapping("/users/{userName}")
    public String getUser(@PathVariable("userName") String userName,
                             Map<String, Object> resultMap) {
        User user = users.getOrDefault(userName, nobodyUser);
        resultMap.put("user", user);
        return "user_detail";
    }
}
