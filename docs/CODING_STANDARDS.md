# Identra Coding Standards

## Backend (Java)

- Prefer records for immutable DTOs
- Constructor injection only for Spring beans
- Explicit nullability; avoid returning null collections
- Package by bounded context under `com.identra.*`
- Fail fast with ProblemDetail (RFC 7807)

## Adapters

- Map vendor payloads only inside adapter modules
- Never log passwords or access tokens
- Implement `CapabilitiesDescriptor` accurately

## Tests

- Unit tests beside production code (`src/test/java`)
- Contract tests against simulators for each adapter
- No network calls in unit tests
