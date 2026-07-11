package com.identra.tenant.web;

import com.identra.tenant.model.Tenant;
import com.identra.tenant.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    public List<Tenant> list() {
        return tenantService.list();
    }

    @GetMapping("/{tenantId}")
    public Tenant get(@PathVariable UUID tenantId) {
        return tenantService.get(tenantId);
    }

    @PostMapping
    public ResponseEntity<Tenant> create(@RequestBody Map<String, String> body) {
        Tenant created = tenantService.create(
                body.get("name"),
                body.get("plan"),
                body.get("deploymentProfile")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{tenantId}/status")
    public Tenant updateStatus(@PathVariable UUID tenantId, @RequestBody Map<String, String> body) {
        return tenantService.updateStatus(tenantId, body.get("status"));
    }
}
