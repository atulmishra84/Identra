package com.identra.adapter.spi;

import java.util.List;
import java.util.UUID;

public interface EntitlementAdapter {

    List<String> listEntitlements(UUID applicationId);

    void assign(UUID accountId, UUID entitlementId);

    void revoke(UUID accountId, UUID entitlementId);
}
