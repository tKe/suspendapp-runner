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

val commonTasks = rootProject.project(":common").tasks
val jvmTask = commonTasks.named<Jar>("shadowJar")
val nonJvmTasks = commonTasks.withType<AbstractExecTask<*>>()
    .matching { it.enabled && (it.name.startsWith("runReleaseExecutable") || it.name.endsWith("NodeRun")) }

tasks.register<Task>("prepareExecutables") {
    dependsOn(jvmTask)
    nonJvmTasks.all { this@register.dependsOn(taskDependencies) }
}

tasks.test {
    dependsOn("prepareExecutables")
    systemProperty("jvmJar", jvmTask.map { it.outputs.files.singleFile.absolutePath }.get())
    nonJvmTasks.all {
        val name = name
        systemProperties(
            "${name}.executable" to executable,
            "${name}.workdir" to workingDir.absolutePath,
        )
        if(this is NodeJsExec) {
            systemProperties("${name}.entrypoint" to inputFileProperty.map { it.asFile.absolutePath }.get())
        }
    }
    systemProperties(
        "kotest.framework.classpath.scanning.config.disable" to "true",
        "kotest.framework.config.fqn" to "KotestConfig",
    )
    systemProperties.onEach(::println)
    useJUnitPlatform()
    outputs.upToDateWhen { false }
}