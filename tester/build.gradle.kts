import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec

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

private val nativeExecutableCompileTask = with(DefaultNativePlatform.host()) {
    when {
        operatingSystem.isMacOsX && architecture.isArm64 -> ":common:linkReleaseExecutableMacosArm64"
        operatingSystem.isMacOsX && architecture.isAmd64 -> ":common:linkReleaseExecutableMacosX64"
        operatingSystem.isLinux && architecture.isArm64 -> ":common:linkReleaseExecutableLinuxArm64"
        operatingSystem.isLinux && architecture.isAmd64 -> ":common:linkReleaseExecutableLinuxX64"
        else -> null
    }
}

val jsRunTasks = provider {
    rootProject.project(":common").tasks
        .filter { it.name.endsWith("NodeRun") }
        .filterIsInstance<NodeJsExec>()
        .filter { it.enabled }
}

tasks.register<Task>("prepareExecutables") {
    dependsOn(":jvm:shadowJar")
    jsRunTasks.get().forEach { dependsOn(it.taskDependencies) }
    nativeExecutableCompileTask?.also { dependsOn(it) }
}

tasks.test {
    dependsOn("prepareExecutables")
    jsRunTasks.get().forEach {
        systemProperties(
            "${it.name}.executable" to it.executable,
            "${it.name}.entrypoint" to it.inputFileProperty.get().asFile.absolutePath,
            "${it.name}.workdir" to it.workingDir.absolutePath,
        )
    }
    systemProperties(
        "kotest.framework.classpath.scanning.config.disable" to "true",
        "kotest.framework.config.fqn" to "KotestConfig",
    )
    useJUnitPlatform()
    outputs.upToDateWhen { false }
}