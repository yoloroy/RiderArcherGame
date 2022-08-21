import com.soywiz.korev.*
import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*

class RiderEventsImpl(
    private val upKey: Key,
    private val downKey: Key,
    private val leftKey: Key,
    private val rightKey: Key
) : RiderEvents {

    private var up = false
    private var down = false
    private var left = false
    private var right = false
    private var shoot: IPoint? = null

    override fun attach(view: View) {
        with(view) {
            mouse {
                down { shoot = it.currentPosStage }
                up { shoot = null }
            }
            keys {
                down(upKey) { up = true }
                up(upKey) { up = false }
                down(downKey) { down = true }
                up(downKey) { down = false }
                down(leftKey) { left = true }
                up(leftKey) { left = false }
                down(rightKey) { right = true }
                up(rightKey) { right = false }
            }
        }
    }

    override fun events() = mutableListOf<RiderEvent>().apply {
        if (up) add(RiderEvent.Move.Up)
        if (down) add(RiderEvent.Move.Down)
        if (left) add(RiderEvent.Move.Left)
        if (right) add(RiderEvent.Move.Right)
        shoot?.let {
            add(RiderEvent.Shoot(it.copy()))
            shoot = null
        }
    }
}
