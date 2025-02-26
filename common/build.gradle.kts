import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenRootPlugin.Companion.kotlinBinaryenExtension

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()
    macosArm64().binaries.executable()
    js(IR) {
        nodejs {
            binaries.executable()
            distribution {

            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("io.arrow-kt:arrow-fx-coroutines:1.2.4")
                implementation("io.arrow-kt:suspendapp:0.4.0")
            }
        }
    }
}