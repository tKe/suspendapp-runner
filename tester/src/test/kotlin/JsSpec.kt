import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FreeSpec
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.reflect.KClass

@EnabledIf(JsSpec.Config::class)
class JsSpec : FreeSpec(), ProcessProvider {
    class Config : JsEntrypointConfig("jsNodeRun")
    val config = Config()

    init {
        include(suspendAppTests(this))
    }

    override fun prepareProcess(mode: String): ProcessBuilder =
        ProcessBuilder(config.executable!!.absolutePathString(), config.entrypoint!!.absolutePathString())
            .directory(config.workdir?.toFile())
            .apply { environment()["TASK"] = mode }
}

open class JsEntrypointConfig(val name: String = "jsNodeRun") : EnabledCondition {
    val executable = System.getProperty("$name.executable")?.let(::Path)
    val entrypoint = System.getProperty("$name.entrypoint")?.let(::Path)
    val workdir = System.getProperty("$name.workdir")?.let(::Path)

    override fun enabled(kclass: KClass<out Spec>): Boolean {
        return (executable?.exists() ?: false)
            && (entrypoint?.exists() ?: false)
            && (workdir?.exists() ?: false)
    }
}