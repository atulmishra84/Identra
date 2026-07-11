package com.identra.adapter.runtime.dispatch;

import com.identra.canonical.Identity;
import com.identra.canonical.ProvisioningRequest;

import java.util.UUID;

public record AdapterDispatchRequest(
        UUID tenantId,
        String adapterId,
        ProvisioningRequest.Operation operation,
        Identity identity,
        String externalId
) {
}
