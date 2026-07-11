rootProject.name = "identra"

include(
    "libs:common",
    "libs:canonical-model",
    "libs:adapter-spi",
    "libs:security",
    "adapters:okta",
    "adapters:sailpoint-isc",
    "adapters:identity-core",
    "services:api-gateway",
    "services:tenant-service",
    "services:identity-service",
    "services:provisioning-service",
    "services:adapter-runtime",
    "services:audit-service",
    "services:approval-service",
    "apps:admin-bff",
)
