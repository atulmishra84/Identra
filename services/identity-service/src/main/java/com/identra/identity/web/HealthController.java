package com.identra.identity.web;

import com.identra.common.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "identity-service");
    }

    @GetMapping("/v1/identities")
    public ResponseEntity<ProblemDetail> listIdentitiesStub() {
        return ResponseEntity.status(501).body(new ProblemDetail(
                "https://identra.example/problems/not-implemented",
                "Not Implemented",
                501,
                "Identity CRUD lands in Sprints 1–3",
                "/v1/identities",
                null
        ));
    }
}
