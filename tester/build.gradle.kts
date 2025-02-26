plugins {
    alias(libs.plugins.kotlinJvm)
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:+")
    testImplementation("io.kotest:kotest-assertions-core:+")
}

tasks.test {
    dependsOn(
        ":jvm:shadowJar",
        ":common:compileProductionExecutableKotlinJs",
        ":common:linkReleaseExecutableMacosArm64",
    )
    useJUnitPlatform()
}