plugins {
    `java-library`
}

dependencies {
    api(project(":libs:adapter-spi"))
    api(project(":libs:canonical-model"))
    implementation(project(":libs:common"))
}
