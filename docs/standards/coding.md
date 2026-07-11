# Coding Standards

## Java (backend)

- Java 21 LTS, Spring Boot 3.x
- Immutable canonical DTOs (records preferred)
- MapStruct for mappings when introduced
- No vendor SDK types outside `adapters/*`
- Prefer constructor injection
- JUnit 5 for tests

## TypeScript (portal)

- TypeScript strict mode
- Next.js App Router; React Server Components where appropriate
- Do not add `useMemo` / `useCallback` unless measured need

## Style

- Meaningful names; no unexplained abbreviations
- Prefer early returns over deep nesting
