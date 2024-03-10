package com.yin.onlineshopping.configuration;

import com.yin.onlineshopping.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {
    @Bean(name = "nobody")
    public User getUserZhang() {
        return new User("Nobody" , "Nobody@");
    }
    @Bean(name = "lisi")
    public User getUserLi() {
        return new User("lisi" , "lisi@hotmail.com");
    }

}
