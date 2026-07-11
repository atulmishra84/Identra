package com.identra.adapter.runtime.web;

import com.identra.adapter.okta.OktaIdentityAdapter;
import com.identra.adapter.runtime.dispatch.AdapterDispatchRequest;
import com.identra.adapter.runtime.dispatch.AdapterDispatchResult;
import com.identra.adapter.runtime.dispatch.AdapterDispatchService;
import com.identra.adapter.sailpoint.isc.SailPointIscIdentityAdapter;
import com.identra.adapter.spi.CapabilitiesDescriptor;
import com.identra.adapter.spi.HealthCapable;
import com.identra.adapter.spi.IdentityAdapter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/adapters")
public class AdapterCatalogController {

    private final AdapterDispatchService dispatchService;

    public AdapterCatalogController(AdapterDispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return dispatchService.adapters().values().stream()
                .map(this::describe)
                .toList();
    }

    @GetMapping("/{adapterId}/health")
    public Map<String, Object> health(@PathVariable String adapterId) {
        IdentityAdapter adapter = dispatchService.adapters().get(adapterId);
        if (adapter == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown adapter");
        }
        HealthCapable.HealthStatus status = adapter instanceof HealthCapable h ? h.health() : HealthCapable.HealthStatus.UNKNOWN;
        return Map.of("adapterId", adapterId, "status", status.name());
    }

    @GetMapping("/capabilities")
    public List<CapabilitiesDescriptor> capabilities() {
        return dispatchService.adapters().values().stream().map(IdentityAdapter::capabilities).toList();
    }

    @PostMapping("/{adapterId}/dispatch")
    public AdapterDispatchResult dispatch(
            @PathVariable String adapterId,
            @RequestBody AdapterDispatchRequest request
    ) {
        AdapterDispatchRequest normalized = new AdapterDispatchRequest(
                request.tenantId(),
                adapterId,
                request.operation(),
                request.identity(),
                request.externalId()
        );
        try {
            return dispatchService.dispatch(normalized);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    private Map<String, Object> describe(IdentityAdapter adapter) {
        CapabilitiesDescriptor caps = adapter.capabilities();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("adapterId", caps.adapterId());
        map.put("vendor", caps.vendor());
        map.put("version", caps.version());
        map.put("capabilities", caps.capabilities());
        map.put("health", adapter instanceof HealthCapable h ? h.health().name() : "UNKNOWN");
        map.put("swapCompatibleWith", caps.adapterId().equals(OktaIdentityAdapter.ADAPTER_ID)
                ? SailPointIscIdentityAdapter.ADAPTER_ID
                : OktaIdentityAdapter.ADAPTER_ID);
        return map;
    }
}
