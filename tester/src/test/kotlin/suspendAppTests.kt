import io.kotest.core.spec.style.freeSpec
import io.kotest.datatest.withData
import io.kotest.inspectors.shouldForOne
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

fun suspendAppTests(processProvider: ProcessProvider) = freeSpec {
    "delay" {
        val (process, output) = processProvider.execute("delay")
        process.exitValue() shouldBe 0
        output.shouldForOne { it.line shouldBe "resource clean complete" }
    }

    "fail" {
        val (process, output) = processProvider.execute("fail")
        process.exitValue() shouldBe 255
        output.shouldForOne { it.line shouldBe "resource clean complete" }
            .shouldForOne { it.line shouldEndWith "IllegalStateException: BOOM!" }
    }

    "child failure" {
        val (process, output) = processProvider.execute("childfail")
        process.exitValue() shouldBe 255
        output.shouldForOne { it.line shouldBe "resource clean complete" }
            .shouldForOne { it.line shouldEndWith "IllegalStateException: boom." }
    }

    "wait and signal" - {
        withData(Signal.entries.asIterable()) { signal ->
            val (process, output) = processProvider.execute("wait") {
                delay(1.seconds)
                sendSignal(signal)
            }
            process.exitValue() shouldBe signal.code + 128
            output.shouldForOne { it.line shouldBe "resource clean complete" }
        }
    }
}