plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()
    macosArm64().binaries.executable()
    js(IR) {
        nodejs()
        binaries.executable()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(libs.arrow.fx)
                implementation(libs.arrow.suspendapp)
            }
        }
    }
}