import com.soywiz.korev.*
import com.soywiz.korio.concurrent.atomic.*

object SessionData {
    var controls by KorAtomicRef(Controls(Key.W, Key.S, Key.A, Key.D))
}

data class Controls(
    val up: Key,
    val down: Key,
    val left: Key,
    val right: Key
)
