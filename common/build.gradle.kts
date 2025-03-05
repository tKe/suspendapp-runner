import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec

plugins {
    alias(libs.plugins.kotlinMultiplatform)
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