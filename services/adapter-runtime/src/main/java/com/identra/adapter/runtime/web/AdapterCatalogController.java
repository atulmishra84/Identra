package com.identra.adapter.runtime.web;

import com.identra.adapter.okta.OktaIdentityAdapter;
import com.identra.adapter.sailpoint.isc.SailPointIscIdentityAdapter;
import com.identra.adapter.spi.CapabilitiesDescriptor;
import com.identra.adapter.spi.HealthCapable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/adapters")
public class AdapterCatalogController {

    private final OktaIdentityAdapter okta = new OktaIdentityAdapter();
    private final SailPointIscIdentityAdapter isc = new SailPointIscIdentityAdapter();

    @GetMapping
    public List<Map<String, Object>> list() {
        return List.of(describe(okta), describe(isc));
    }

    @GetMapping("/{adapterId}/health")
    public Map<String, Object> health(@PathVariable String adapterId) {
        HealthCapable.HealthStatus status = switch (adapterId) {
            case "okta" -> okta.health();
            case "sailpoint-isc" -> isc.health();
            default -> HealthCapable.HealthStatus.DOWN;
        };
        return Map.of(
                "adapterId", adapterId,
                "status", status.name()
        );
    }

    @GetMapping("/capabilities")
    public List<CapabilitiesDescriptor> capabilities() {
        return List.of(okta.capabilities(), isc.capabilities());
    }

    private static Map<String, Object> describe(OktaIdentityAdapter adapter) {
        return describe(adapter.capabilities(), adapter.health());
    }

    private static Map<String, Object> describe(SailPointIscIdentityAdapter adapter) {
        return describe(adapter.capabilities(), adapter.health());
    }

    private static Map<String, Object> describe(CapabilitiesDescriptor caps, HealthCapable.HealthStatus health) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("adapterId", caps.adapterId());
        map.put("vendor", caps.vendor());
        map.put("version", caps.version());
        map.put("capabilities", caps.capabilities());
        map.put("health", health.name());
        return map;
    }
}
