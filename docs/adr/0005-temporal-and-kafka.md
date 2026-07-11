# ADR-0005: Temporal and Kafka

- **Status:** Accepted
- **Date:** 2026-07-11
- **Deciders:** Platform Architecture

## Context

Provisioning, approvals, migrations, and connector validation are long-running and need compensation. Events also need fan-out to audit, search, and notifications.

## Decision

- **Temporal** owns durable execution (workflows/activities)
- **Kafka** owns the event backbone (CloudEvents, transactional outbox)
- Do not reinvent a custom workflow engine

## Consequences

- Clear separation: orchestration vs event streaming
- Operational cost of two infrastructure systems
- Better reliability for human-in-the-loop approvals
