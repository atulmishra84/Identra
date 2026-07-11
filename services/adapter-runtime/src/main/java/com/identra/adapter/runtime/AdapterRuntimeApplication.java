package com.identra.adapter.runtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.identra")
public class AdapterRuntimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdapterRuntimeApplication.class, args);
    }
}
