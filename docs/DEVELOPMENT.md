# Identra Development Standards

## Principles

- OpenAPI / protobuf first for public and internal contracts
- ADR required for cross-cutting decisions (`docs/adr`)
- Conventional commits; semver for adapter artifacts
- Definition of Done: tests, OpenTelemetry hooks, runbook touch when needed
- Feature flags for incomplete behavior

## Java

- Java 21, Spring Boot 3.x
- Immutable canonical DTOs in `libs/canonical-model`
- **No vendor types outside `adapters/*`**
- MapStruct for mapping (when introduced)
- Spotless / Checkstyle / ErrorProne (wire in Sprint 1)

## Frontend

- TypeScript strict
- Next.js App Router
- Prefer React Compiler-friendly patterns; avoid unnecessary `useMemo` / `useCallback`

## Git

- Trunk-based development with short-lived PRs
- Never commit secrets (`.env`, keys, connection strings)
