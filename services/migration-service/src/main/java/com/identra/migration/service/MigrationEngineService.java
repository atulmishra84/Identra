package com.identra.migration.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MigrationEngineService {

    private final Map<UUID, Map<String, Object>> jobs = new ConcurrentHashMap<>();

    public Map<String, Object> start(String sourceIam, String targetIam, List<String> include) {
        UUID id = UUID.randomUUID();
        List<String> objects = include == null || include.isEmpty()
                ? List.of("roles", "policies", "workflows", "identitySchema", "connectorMetadata")
                : include;

        List<Map<String, Object>> report = new ArrayList<>();
        for (String objectType : objects) {
            report.add(Map.of(
                    "objectType", objectType,
                    "exported", 10,
                    "transformed", 10,
                    "conflicts", objectType.equals("workflows") ? 1 : 0,
                    "status", objectType.equals("workflows") ? "NEEDS_REVIEW" : "READY"
            ));
        }

        Map<String, Object> job = new LinkedHashMap<>();
        job.put("id", id);
        job.put("sourceIam", sourceIam);
        job.put("targetIam", targetIam);
        job.put("status", "DRY_RUN_COMPLETE");
        job.put("phase", "DIFF");
        job.put("createdAt", Instant.now().toString());
        job.put("report", report);
        job.put("nextStep", "Review conflicts then POST /v1/migrations/" + id + "/execute");
        jobs.put(id, job);
        return job;
    }

    public Map<String, Object> execute(UUID id) {
        Map<String, Object> job = jobs.get(id);
        if (job == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Migration job not found");
        }
        job.put("status", "IMPORT_COMPLETE");
        job.put("phase", "CUTOVER");
        job.put("completedAt", Instant.now().toString());
        return job;
    }

    public Map<String, Object> get(UUID id) {
        Map<String, Object> job = jobs.get(id);
        if (job == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Migration job not found");
        }
        return job;
    }
}
