plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.autosignin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    
    // HTTP client for network requests
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    
    // JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Logging
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.6")
    
    // Testing
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("com.autosignin.MainKt")
}