plugins {
    kotlin("jvm") version "1.9.24"
    application
}

group = "com.bytebank"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.bytebank.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
