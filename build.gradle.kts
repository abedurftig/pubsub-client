plugins {
    java
    kotlin("jvm") version "1.5.21"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(platform("com.google.cloud:libraries-bom:20.8.0"))

    implementation("com.google.cloud:google-cloud-pubsub")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}