package com.zomiggy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ZomiggyApplication {
    public static void main(String[] args) { SpringApplication.run(ZomiggyApplication.class, args); }
}