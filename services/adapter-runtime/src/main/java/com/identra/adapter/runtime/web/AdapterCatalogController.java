package com.identra.adapter.runtime.web;

import com.identra.adapter.okta.OktaIdentityAdapter;
import com.identra.adapter.sailpoint.isc.SailPointIscIdentityAdapter;
import com.identra.adapter.spi.CapabilitiesDescriptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AdapterCatalogController {

    private final OktaIdentityAdapter okta = new OktaIdentityAdapter();
    private final SailPointIscIdentityAdapter isc = new SailPointIscIdentityAdapter();

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "adapter-runtime");
    }

    @GetMapping("/v1/adapters/capabilities")
    public List<CapabilitiesDescriptor> capabilities() {
        return List.of(okta.capabilities(), isc.capabilities());
    }
}
