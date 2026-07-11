package com.identra.adapter.runtime.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/actuator-alias/health")
    public Map<String, String> healthAlias() {
        return Map.of("status", "UP", "service", "adapter-runtime");
    }
}
