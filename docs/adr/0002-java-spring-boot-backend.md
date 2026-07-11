# ADR-0002: Java Spring Boot backend

- **Status:** Accepted
- **Date:** 2026-07-11
- **Deciders:** Platform Architecture

## Context

The plan allowed Java Spring Boot or .NET. IAM ecosystems (SailPoint, ForgeRock, many connectors) are Java-heavy; enterprise IAM talent skews Java.

## Decision

- Core platform services: **Java 21 LTS + Spring Boot 3.x**
- Build: Gradle composite monorepo
- Customer SDKs may include .NET later; core remains Java

## Consequences

- Strong adapter ecosystem alignment
- Single JVM skill set across Fabric services
- Frontend remains TypeScript/Next.js (separate)
