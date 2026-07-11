package com.identra.identity;

import com.identra.canonical.Identity;
import com.identra.common.ProblemDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sprint 0 skeleton: in-memory identity store. Replace with PostgreSQL in Sprints 1–3.
 */
@RestController
@RequestMapping("/v1/identities")
public class IdentityController {

    private final Map<UUID, Identity> store = new ConcurrentHashMap<>();

    @GetMapping("/{id}")
    public ResponseEntity<Identity> get(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID id
    ) {
        Identity identity = store.get(id);
        if (identity == null || !tenantId.equals(identity.tenantId())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(identity);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(new ProblemDetail(
                        "about:blank",
                        "Bad Request",
                        400,
                        ex.getMessage(),
                        null,
                        null,
                        false,
                        null
                ));
    }
}
