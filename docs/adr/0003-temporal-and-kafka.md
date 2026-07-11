# ADR 0003: Temporal + Kafka

## Status

Accepted

## Context

Provisioning, approvals, and migrations are long-running and must survive failures. Domain events must fan out to audit, search, and webhooks.

## Decision

- **Temporal** owns durable workflow execution (provisioning, access request, migration jobs)
- **Kafka** (CloudEvents) is the event backbone with transactional outbox from PostgreSQL

## Consequences

- Do not reinvent a custom workflow engine
- Kafka is not a substitute for durable orchestration
- Partition identity streams by `tenantId:identityId`
