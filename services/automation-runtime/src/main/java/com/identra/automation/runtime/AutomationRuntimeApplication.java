package com.identra.automation.runtime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.identra.automation.runtime")
public class AutomationRuntimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutomationRuntimeApplication.class, args);
    }
}
