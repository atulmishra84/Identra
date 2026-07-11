rootProject.name = "identra"

// Sprint 0 / MVP modules — additional services land in later sprints
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
    "apps:admin-bff",
)
