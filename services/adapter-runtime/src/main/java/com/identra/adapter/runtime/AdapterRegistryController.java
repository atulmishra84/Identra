package com.identra.adapter.runtime;

import com.identra.adapter.okta.OktaIdentityAdapter;
import com.identra.adapter.sailpoint.isc.SailPointIscIdentityAdapter;
import com.identra.adapter.spi.CapabilitiesDescriptor;
import com.identra.adapter.spi.HealthCapable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/adapters")
public class AdapterRegistryController {

    private final OktaIdentityAdapter okta = new OktaIdentityAdapter();
    private final SailPointIscIdentityAdapter isc = new SailPointIscIdentityAdapter();

    @GetMapping
    public List<Map<String, Object>> list() {
        return List.of(describe(okta.capabilities(), okta.health()), describe(isc.capabilities(), isc.health()));
    }

    @GetMapping("/{vendorKey}/health")
    public HealthCapable.AdapterHealth health(@PathVariable String vendorKey) {
        return switch (vendorKey) {
            case OktaIdentityAdapter.VENDOR_KEY -> okta.health();
            case SailPointIscIdentityAdapter.VENDOR_KEY -> isc.health();
            default -> HealthCapable.AdapterHealth.down("Unknown vendor: " + vendorKey);
        };
    }

    private static Map<String, Object> describe(CapabilitiesDescriptor caps, HealthCapable.AdapterHealth health) {
        return Map.of(
                "vendorKey", caps.vendorKey(),
                "displayName", caps.displayName(),
                "capabilities", caps.capabilities(),
                "health", health
        );
    }
}
