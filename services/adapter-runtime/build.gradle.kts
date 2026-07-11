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
    implementation(project(":adapters:entra"))
    implementation(project(":adapters:saviynt"))
    implementation(project(":adapters:ping"))
    implementation(project(":adapters:oracle-iam"))
    implementation(project(":adapters:ibm-verify"))
    implementation(project(":adapters:forgerock"))
    implementation(project(":adapters:midpoint"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
