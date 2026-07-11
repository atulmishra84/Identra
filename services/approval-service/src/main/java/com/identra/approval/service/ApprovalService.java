package com.identra.approval.service;

import com.identra.approval.model.ApprovalTask;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApprovalService {

    private final Map<UUID, ApprovalTask> tasks = new ConcurrentHashMap<>();

    public ApprovalTask create(UUID tenantId, UUID jobId, String title, String requestedBy) {
        UUID id = UUID.randomUUID();
        ApprovalTask task = new ApprovalTask(
                id,
                tenantId,
                jobId,
                title == null ? "Access / provisioning approval" : title,
                "PENDING",
                requestedBy,
                null,
                Instant.now(),
                null
        );
        tasks.put(id, task);
        return task;
    }

    public List<ApprovalTask> list(UUID tenantId) {
        return tasks.values().stream().filter(t -> tenantId.equals(t.tenantId())).toList();
    }

    public ApprovalTask decide(UUID tenantId, UUID taskId, String decision, String approver) {
        ApprovalTask existing = tasks.get(taskId);
        if (existing == null || !tenantId.equals(existing.tenantId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Approval task not found");
        }
        if (!"PENDING".equals(existing.status())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Approval already decided");
        }
        if (!List.of("APPROVED", "DENIED").contains(decision)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "decision must be APPROVED or DENIED");
        }
        ApprovalTask updated = new ApprovalTask(
                existing.id(),
                existing.tenantId(),
                existing.jobId(),
                existing.title(),
                decision,
                existing.requestedBy(),
                approver,
                existing.createdAt(),
                Instant.now()
        );
        tasks.put(taskId, updated);
        return updated;
    }
}
