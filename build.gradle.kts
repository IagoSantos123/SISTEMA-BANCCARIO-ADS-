plugins {
    kotlin("jvm") version "1.9.24"
    id("org.jetbrains.compose") version "1.6.11"
}

group = "com.bytebank"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(17)
}

compose.desktop {
    application {
        mainClass = "com.bytebank.ui.MainUiKt"
    }
}

tasks.test {
    useJUnitPlatform()
}

// A interface gráfica (Compose Desktop) roda com './gradlew run'.
// Esta task mantém disponível a versão original de console, exigida pelo exercício.
tasks.register<JavaExec>("runConsole") {
    group = "application"
    description = "Executa a versão de console (Main.kt) do sistema bancário."
    mainClass.set("com.bytebank.MainKt")
    classpath = sourceSets["main"].runtimeClasspath
}
