import com.sun.jna.Platform
import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FreeSpec
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.reflect.KClass

@EnabledIf(NativeEntrypointConfig::class)
class NativeSpec : FreeSpec(), ProcessProvider {
    val config = NativeEntrypointConfig()
    init {
        include(suspendAppTests(this))
    }

    override fun prepareProcess(mode: String) =
        ProcessBuilder(config.executable!!.pathString, mode)
            .directory(config.workdir?.toFile())
}

class NativeEntrypointConfig() : EnabledCondition {
    val name = System.getProperties().stringPropertyNames()
        .first { it.startsWith("runReleaseExecutable") && it.endsWith(".executable") }
        .substringBeforeLast('.')
    val executable = System.getProperty("$name.executable")?.let(::Path)
    val workdir = System.getProperty("$name.workdir")?.let(::Path)

    override fun enabled(kclass: KClass<out Spec>): Boolean {
        return (executable?.exists() ?: false)
            && (workdir?.exists() ?: false)
    }
}
