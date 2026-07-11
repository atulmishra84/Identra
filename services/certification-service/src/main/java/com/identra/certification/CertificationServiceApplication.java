package com.identra.certification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.identra.certification")
public class CertificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CertificationServiceApplication.class, args);
    }
}
