plugins {
    `java-library`
}

dependencies {
    api(project(":libs:common"))
    api("com.fasterxml.jackson.core:jackson-annotations:2.18.2")
    api("jakarta.validation:jakarta.validation-api:3.1.0")
}
