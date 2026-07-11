package com.identra.adapter.spi;

import java.util.UUID;

/** Passwords are never stored in Fabric; adapters set credentials at the vendor. */
public interface PasswordAdapter {

    void setPassword(UUID accountId, char[] password);

    void expirePassword(UUID accountId);
}
