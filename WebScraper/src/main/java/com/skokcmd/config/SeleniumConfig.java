package com.skokcmd.config;

import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class SeleniumConfig {

    @PostConstruct
    void postConstruct() {
        // replace prop value with env variable
        System.setProperty("webdriver.chrome.driver", "/Users/skokcmd/Documents/chromedriver");
    }

    @Bean
    public ChromeDriver chromeDriver() {
        return new ChromeDriver();
    }

}
