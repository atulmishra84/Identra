package com.identra.certification.web;

import com.identra.certification.service.CertificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/certifications")
public class CertificationController {

    private final CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @GetMapping
    public List<Map<String, Object>> list(@RequestHeader("X-Tenant-Id") UUID tenantId) {
        return certificationService.list(tenantId);
    }

    @PostMapping("/campaigns")
    public ResponseEntity<Map<String, Object>> create(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestBody Map<String, Object> body
    ) {
        @SuppressWarnings("unchecked")
        List<String> reviewers = body.get("reviewers") instanceof List<?> list
                ? list.stream().map(String::valueOf).toList()
                : List.of();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                certificationService.createCampaign(tenantId, String.valueOf(body.get("name")), reviewers)
        );
    }

    @GetMapping("/campaigns/{id}")
    public Map<String, Object> get(@PathVariable UUID id) {
        return certificationService.get(id);
    }

    @PostMapping("/sod/evaluate")
    public Map<String, Object> sod(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<String> entitlements = body.get("entitlements") instanceof List<?> list
                ? list.stream().map(String::valueOf).toList()
                : List.of();
        return certificationService.evaluateSoD(entitlements);
    }
}
