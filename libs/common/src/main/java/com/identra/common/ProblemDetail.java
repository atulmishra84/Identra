package com.identra.common;

/** RFC 7807-inspired problem detail for API errors. */
public record ProblemDetail(
        String type,
        String title,
        int status,
        String detail,
        String instance,
        String correlationId
) {}
