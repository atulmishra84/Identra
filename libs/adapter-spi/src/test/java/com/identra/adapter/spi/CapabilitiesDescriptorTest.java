package com.identra.adapter.spi;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CapabilitiesDescriptorTest {

    @Test
    void descriptorHoldsCapabilities() {
        var descriptor = new CapabilitiesDescriptor(
                "okta",
                "Okta",
                "0.1.0",
                EnumSet.of(CapabilitiesDescriptor.Capability.IDENTITY_CRUD)
        );

        assertEquals("okta", descriptor.adapterId());
        assertTrue(descriptor.capabilities().contains(CapabilitiesDescriptor.Capability.IDENTITY_CRUD));
    }
}
