package com.identra.governance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.identra.governance")
public class GovernanceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GovernanceServiceApplication.class, args);
    }
}
