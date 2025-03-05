import io.kotest.core.spec.style.FreeSpec

class JvmSpec : FreeSpec(), ProcessProvider {
    init {
        include(suspendAppTests(this))
    }

    override fun prepareProcess(mode: String) =
        ProcessBuilder("java", "-jar", "../jvm/build/libs/jvm-all.jar", mode)
}