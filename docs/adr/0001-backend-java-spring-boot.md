# ADR 0001: Backend Java 21 + Spring Boot

## Status

Accepted

## Context

Identra needs an enterprise backend for IAM-adjacent workloads. Candidates were Java Spring Boot and .NET.

## Decision

Use **Java 21 LTS** and **Spring Boot 3.x** for all platform services and first-party adapters.

## Consequences

- Aligns with SailPoint/ForgeRock talent pools and Java IAM SDKs
- Customer .NET SDKs may be published later; core remains JVM
- Gradle composite monorepo for services, libs, and adapters
