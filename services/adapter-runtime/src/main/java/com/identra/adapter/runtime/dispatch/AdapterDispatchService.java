package com.identra.adapter.runtime.dispatch;

import com.identra.adapter.entra.EntraIdentityAdapter;
import com.identra.adapter.forgerock.ForgeRockIdentityAdapter;
import com.identra.adapter.ibm.IbmVerifyIdentityAdapter;
import com.identra.adapter.midpoint.MidPointIdentityAdapter;
import com.identra.adapter.okta.OktaClientConfig;
import com.identra.adapter.okta.OktaIdentityAdapter;
import com.identra.adapter.oracle.OracleIamIdentityAdapter;
import com.identra.adapter.ping.PingIdentityAdapter;
import com.identra.adapter.sailpoint.isc.SailPointIscClientConfig;
import com.identra.adapter.sailpoint.isc.SailPointIscIdentityAdapter;
import com.identra.adapter.saviynt.SaviyntIdentityAdapter;
import com.identra.adapter.spi.IdentityAdapter;
import com.identra.canonical.Identity;
import com.identra.canonical.ProvisioningRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdapterDispatchService {

    private final Map<String, IdentityAdapter> adapters = new ConcurrentHashMap<>();

    public AdapterDispatchService(
            @Value("${identra.adapters.okta.dry-run:true}") boolean oktaDryRun,
            @Value("${identra.adapters.okta.org-url:https://example.okta.com}") String oktaOrgUrl,
            @Value("${identra.adapters.okta.api-token:}") String oktaToken,
            @Value("${identra.adapters.isc.dry-run:true}") boolean iscDryRun,
            @Value("${identra.adapters.isc.base-url:https://example.api.identitynow.com}") String iscBaseUrl,
            @Value("${identra.adapters.isc.access-token:}") String iscToken,
            @Value("${identra.adapters.entra.dry-run:true}") boolean entraDryRun
    ) {
        UUID demoTenant = UUID.fromString("11111111-1111-1111-1111-111111111111");
        adapters.put(OktaIdentityAdapter.ADAPTER_ID,
                new OktaIdentityAdapter(new OktaClientConfig(oktaOrgUrl, oktaToken, oktaDryRun), demoTenant));
        adapters.put(SailPointIscIdentityAdapter.ADAPTER_ID,
                new SailPointIscIdentityAdapter(new SailPointIscClientConfig(iscBaseUrl, "client", iscToken, iscDryRun), demoTenant));
        adapters.put(EntraIdentityAdapter.ADAPTER_ID, new EntraIdentityAdapter(entraDryRun, demoTenant));
        adapters.put(SaviyntIdentityAdapter.ADAPTER_ID, new SaviyntIdentityAdapter(demoTenant));
        adapters.put(PingIdentityAdapter.ADAPTER_ID, new PingIdentityAdapter(demoTenant));
        adapters.put(OracleIamIdentityAdapter.ADAPTER_ID, new OracleIamIdentityAdapter(demoTenant));
        adapters.put(IbmVerifyIdentityAdapter.ADAPTER_ID, new IbmVerifyIdentityAdapter(demoTenant));
        adapters.put(ForgeRockIdentityAdapter.ADAPTER_ID, new ForgeRockIdentityAdapter(demoTenant));
        adapters.put(MidPointIdentityAdapter.ADAPTER_ID, new MidPointIdentityAdapter(demoTenant));
    }

    public AdapterDispatchResult dispatch(AdapterDispatchRequest request) {
        IdentityAdapter adapter = adapters.get(request.adapterId());
        if (adapter == null) {
            throw new IllegalArgumentException("Unknown adapter: " + request.adapterId());
        }
        return switch (request.operation()) {
            case CREATE -> new AdapterDispatchResult("SUCCEEDED", "Created via " + request.adapterId(),
                    adapter.create(request.identity()));
            case UPDATE, ENABLE -> new AdapterDispatchResult("SUCCEEDED", "Updated via " + request.adapterId(),
                    adapter.update(request.identity()));
            case DISABLE -> {
                adapter.disable(requireExternalId(request));
                yield new AdapterDispatchResult("SUCCEEDED", "Disabled via " + request.adapterId(), null);
            }
            case DELETE -> {
                adapter.delete(requireExternalId(request));
                yield new AdapterDispatchResult("SUCCEEDED", "Deleted via " + request.adapterId(), null);
            }
            case RESET_PASSWORD, ASSIGN_ENTITLEMENT, REVOKE_ENTITLEMENT ->
                    new AdapterDispatchResult("SUCCEEDED",
                            "Operation " + request.operation() + " accepted", null);
        };
    }

    public Map<String, IdentityAdapter> adapters() {
        return Map.copyOf(adapters);
    }

    private static String requireExternalId(AdapterDispatchRequest request) {
        if (request.externalId() != null && !request.externalId().isBlank()) {
            return request.externalId();
        }
        if (request.identity() != null && request.identity().externalIds() != null) {
            return request.identity().externalIds().stream()
                    .findFirst()
                    .map(Identity.ExternalId::value)
                    .orElseThrow(() -> new IllegalArgumentException("externalId required"));
        }
        throw new IllegalArgumentException("externalId required");
    }
}
