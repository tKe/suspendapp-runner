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
                api(kotlin("stdlib"))
                api(libs.arrow.fx)
                api(libs.arrow.suspendapp)
            }
        }
    }
}