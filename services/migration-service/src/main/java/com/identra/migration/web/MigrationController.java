package com.identra.migration.web;

import com.identra.migration.service.MigrationEngineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/migrations")
public class MigrationController {

    private final MigrationEngineService migrationEngine;

    public MigrationController(MigrationEngineService migrationEngine) {
        this.migrationEngine = migrationEngine;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> start(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<String> include = body.get("include") instanceof List<?> list
                ? list.stream().map(String::valueOf).toList()
                : List.of();
        Map<String, Object> job = migrationEngine.start(
                String.valueOf(body.get("sourceIam")),
                String.valueOf(body.get("targetIam")),
                include
        );
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(job);
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable UUID id) {
        return migrationEngine.get(id);
    }

    @PostMapping("/{id}/execute")
    public Map<String, Object> execute(@PathVariable UUID id) {
        return migrationEngine.execute(id);
    }
}
