package com.identra.approval.web;

import com.identra.approval.model.ApprovalTask;
import com.identra.approval.service.ApprovalService;
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
@RequestMapping("/v1/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping
    public List<ApprovalTask> list(@RequestHeader("X-Tenant-Id") UUID tenantId) {
        return approvalService.list(tenantId);
    }

    @PostMapping
    public ResponseEntity<ApprovalTask> create(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @RequestBody Map<String, String> body
    ) {
        UUID jobId = body.get("jobId") == null ? UUID.randomUUID() : UUID.fromString(body.get("jobId"));
        ApprovalTask task = approvalService.create(tenantId, jobId, body.get("title"), body.get("requestedBy"));
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PostMapping("/{taskId}/decision")
    public ApprovalTask decide(
            @RequestHeader("X-Tenant-Id") UUID tenantId,
            @PathVariable UUID taskId,
            @RequestBody Map<String, String> body
    ) {
        return approvalService.decide(tenantId, taskId, body.get("decision"), body.get("approver"));
    }
}
