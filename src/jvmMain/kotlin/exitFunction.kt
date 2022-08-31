import kotlin.system.*

actual val exitFunction: (() -> Unit)? = { exitProcess(0) }
