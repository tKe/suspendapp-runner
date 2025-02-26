plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.shadow)
    application
}

dependencies {
    implementation(projects.common)
}

application {
    mainClass = "MainKt"
}

kotlin {
    jvmToolchain(21)
}