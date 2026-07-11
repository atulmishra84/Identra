plugins {
    `java-library`
}

dependencies {
    api(project(":libs:common"))
    api("com.nimbusds:nimbus-jose-jwt:9.40")
    api("com.fasterxml.jackson.core:jackson-databind:2.18.2")
}
