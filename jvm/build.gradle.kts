plugins {
    alias(libs.plugins.kotlinJvm)
    application
    id("com.gradleup.shadow") version "9.0.0-beta9"
}

dependencies {
    implementation(projects.common)
    implementation("io.arrow-kt:suspendapp:0.4.0")
}

application {
    mainClass = "MainKt"
}

kotlin {
    jvmToolchain(21)
}