package com.identra.automation.factory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.identra.automation.factory")
public class AutomationFactoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutomationFactoryApplication.class, args);
    }
}
