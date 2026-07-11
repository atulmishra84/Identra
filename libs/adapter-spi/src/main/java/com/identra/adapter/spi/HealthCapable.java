package com.identra.adapter.spi;

public interface HealthCapable {

    HealthStatus health();

    enum HealthStatus {
        UNKNOWN,
        UP,
        DEGRADED,
        DOWN
    }
}
