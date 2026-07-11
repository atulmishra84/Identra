package com.identra.factory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.identra.factory")
public class ConnectorFactoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConnectorFactoryApplication.class, args);
    }
}
