import io.kotest.core.config.AbstractProjectConfig
import io.kotest.engine.concurrency.SpecExecutionMode

object KotestConfig : AbstractProjectConfig() {
    override val specExecutionMode = SpecExecutionMode.Concurrent
}