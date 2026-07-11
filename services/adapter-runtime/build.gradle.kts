plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":libs:adapter-spi"))
    implementation(project(":libs:canonical-model"))
    implementation(project(":adapters:okta"))
    implementation(project(":adapters:sailpoint-isc"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
