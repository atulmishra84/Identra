package com.identra.approval.model;

import java.time.Instant;
import java.util.UUID;

public record ApprovalTask(
        UUID id,
        UUID tenantId,
        UUID jobId,
        String title,
        String status,
        String requestedBy,
        String approver,
        Instant createdAt,
        Instant decidedAt
) {
}
