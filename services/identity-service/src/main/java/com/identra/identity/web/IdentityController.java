package com.identra.identity.web;

import com.identra.canonical.Identity;
import com.identra.canonical.IdentityWriteRequest;
import com.identra.common.ProblemDetail;
import com.identra.identity.service.IdentityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/identities")
public class IdentityController {

    private final IdentityService identityService;

    public IdentityController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @GetMapping
    public Map<String, Object> list(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestParam(defaultValue = "1") int startIndex,
            @RequestParam(defaultValue = "100") int count
    ) {
        List<Identity> resources = identityService.list(tenantId, startIndex, count);
        return Map.of(
                "totalResults", identityService.count(tenantId),
                "startIndex", startIndex,
                "itemsPerPage", resources.size(),
                "Resources", resources
        );
    }

    @GetMapping("/{id}")
    public Identity get(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id
    ) {
        return identityService.get(tenantId, id);
    }

    @PostMapping
    public ResponseEntity<Identity> create(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @Valid @RequestBody IdentityWriteRequest request
    ) {
        Identity created = identityService.create(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public Identity replace(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id,
            @Valid @RequestBody IdentityWriteRequest request
    ) {
        return identityService.replace(tenantId, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id
    ) {
        identityService.delete(tenantId, id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(new ProblemDetail(
                        "about:blank",
                        status.getReasonPhrase(),
                        status.value(),
                        ex.getReason(),
                        null,
                        null
                ));
    }
}
