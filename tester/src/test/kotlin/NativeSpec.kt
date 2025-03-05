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

@EnabledIf(HasNativeBinary::class)
class NativeSpec : FreeSpec(), ProcessProvider {
    init {
        include(suspendAppTests(this))
    }

    override fun prepareProcess(mode: String) =
        ProcessBuilder(nativeBinary!!.pathString, mode)
}

class HasNativeBinary : EnabledCondition {
    override fun enabled(kclass: KClass<out Spec>) =
        nativeBinary?.exists() == true
}

private val nativeTarget = run {
    when (Platform.getOSType()) {
        Platform.MAC -> when (Platform.ARCH) {
            "aarch64" -> "macosArm64"
            "x86-64" -> "macosX64"
            else -> null
        }
        Platform.LINUX -> when (Platform.ARCH) { // untested
            "aarch64" -> "linuxArm64"
            "x86-64" -> "linuxX64"
            else -> null
        }
        else -> null
    }
}

private val nativeBinary = nativeTarget?.let { Path("../common/build/bin/$it/releaseExecutable/common.kexe").absolute() }
