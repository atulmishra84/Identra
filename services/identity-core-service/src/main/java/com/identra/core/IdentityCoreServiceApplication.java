package com.identra.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.identra.core")
public class IdentityCoreServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityCoreServiceApplication.class, args);
    }
}
