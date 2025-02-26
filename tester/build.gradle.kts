plugins {
    alias(libs.plugins.kotlinJvm)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
}

tasks.test {
    dependsOn(
        ":jvm:shadowJar",
        ":common:compileProductionExecutableKotlinJs",
        ":common:linkReleaseExecutableMacosArm64",
    )
    useJUnitPlatform()
}