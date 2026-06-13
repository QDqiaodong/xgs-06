package com.leathercraft;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.leathercraft.mapper")
public class LeatherCraftApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeatherCraftApplication.class, args);
    }
}
