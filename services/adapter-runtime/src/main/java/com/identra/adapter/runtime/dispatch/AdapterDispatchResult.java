package com.identra.adapter.runtime.dispatch;

import com.identra.canonical.Identity;

public record AdapterDispatchResult(
        String status,
        String detail,
        Identity identity
) {
}
