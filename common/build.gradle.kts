import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.shadow)
}

kotlin {
    jvm()
    macosX64().binaries.executable()
    macosArm64().binaries.executable()
    linuxArm64().binaries.executable()
    linuxX64().binaries.executable()
    js(IR) {
        nodejs()
        binaries.executable()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                api(kotlin("stdlib"))
                api(libs.arrow.fx)
                api(libs.arrow.suspendapp)
            }
        }
    }
}

tasks.named<ShadowJar>("shadowJar") {
    manifest {
        attributes("Main-Class" to "MainKt")
    }
}