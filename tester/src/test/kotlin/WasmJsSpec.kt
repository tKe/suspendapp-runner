import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.style.FreeSpec
import kotlin.io.path.absolutePathString

@EnabledIf(WasmJsSpec.Config::class)
class WasmJsSpec : FreeSpec(), ProcessProvider {
    class Config : JsEntrypointConfig("wasmJsNodeRun")
    val config = Config()

    init {
        include(suspendAppTests(this))
    }

    override fun prepareProcess(mode: String): ProcessBuilder =
        ProcessBuilder(config.executable!!.absolutePathString(), config.entrypoint!!.absolutePathString())
            .directory(config.workdir?.toFile())
            .apply { environment()["TASK"] = mode }
}