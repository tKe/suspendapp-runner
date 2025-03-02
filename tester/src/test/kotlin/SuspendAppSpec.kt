import io.kotest.assertions.fail
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

enum class Runner(val prepareProcess: (mode: String) -> ProcessBuilder) {
    JVM({ ProcessBuilder("java", "-jar", "../jvm/build/libs/jvm-all.jar", it) }),
    NATIVE({ ProcessBuilder("../common/build/bin/macosX64/releaseExecutable/common.kexe", it) }),
    JS({
        ProcessBuilder(
            "node",
            "../common/build/compileSync/js/main/productionExecutable/kotlin/suspendapp-bug-common.js"
        )
            .apply { environment()["TASK"] = it }
    }),
}

data class OutputLine(val line: String, val source: String)

suspend fun Runner.execute(
    mode: String,
    timeout: Duration = 10.seconds,
    interact: suspend Process.() -> Unit = {}
) = withContext(Dispatchers.IO) {
    val process = prepareProcess(mode)
        .start()

    println("Started: ${process.info()} ($mode)")

    @OptIn(ExperimentalCoroutinesApi::class)
    val outputChannel = produce {
        launch { process.inputReader().useLines { lines -> lines.forEach { send(OutputLine(it, "stdout")) } } }
        launch { process.errorReader().useLines { lines -> lines.forEach { send(OutputLine(it, "stderr")) } } }
    }

    val output = async {
        outputChannel.consumeAsFlow()
            .onEach { println("[${this@execute}:${it.source}] ${it.line}") }
            .toList()
    }

    val interaction = launch { process.interact() }
    if (!process.waitFor(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)) {
        process.destroyForcibly()
        process.waitFor()
        fail("process didn't finish")
    }
    interaction.cancel()

    process to output.await()
}

fun Process.sendSignal(signal: Signal) {
    Runtime.getRuntime().exec(arrayOf("kill", "-$signal", pid().toString())).waitFor()
}

class SuspendAppSpec : FreeSpec({
    withData(Runner.entries) { runner ->
        "delay" {
            val (process, output) = runner.execute("delay")
            process.exitValue() shouldBe 0
            output.shouldForOne { it.line shouldBe "resource clean complete" }
        }

        "fail" {
            val (process, output) = runner.execute("fail")
            process.exitValue() shouldBe 255
            output.shouldForOne { it.line shouldBe "resource clean complete" }
                .shouldForOne { it.line shouldEndWith "IllegalStateException: BOOM!" }
        }

        "child failure" {
            val (process, output) = runner.execute("childfail")
            process.exitValue() shouldBe 255
            output.shouldForOne { it.line shouldBe "resource clean complete" }
                .shouldForOne { it.line shouldEndWith "IllegalStateException: boom." }
        }

        "wait and signal" - {
            withData(Signal.entries.asIterable()) { signal ->
                val (process, output) = runner.execute("wait") {
                    delay(1.seconds)
                    sendSignal(signal)
                }
                process.exitValue() shouldBe signal.code + 128
                output.shouldForOne { it.line shouldBe "resource clean complete" }
            }
        }
    }
})

enum class Signal(val code: Int) {
    SIGINT(2),
    SIGTERM(15);
}
