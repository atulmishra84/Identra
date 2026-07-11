plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":libs:canonical-model"))
    implementation(project(":libs:adapter-spi"))
    implementation(project(":libs:security"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.temporal:temporal-sdk:1.27.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
