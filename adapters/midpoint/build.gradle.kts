plugins {
    `java-library`
}

dependencies {
    api(project(":libs:adapter-spi"))
    api(project(":libs:canonical-model"))
    implementation("org.slf4j:slf4j-api:2.0.16")
}
