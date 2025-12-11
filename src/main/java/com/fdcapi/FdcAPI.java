package com.fdcapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.fdcapi", "com.fdcdatax"})
public class FdcAPI {
    public static void main(String[] args) {
        SpringApplication.run(FdcAPI.class, args);
    }
}