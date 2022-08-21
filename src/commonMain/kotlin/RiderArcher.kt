import com.soywiz.klock.*
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*

class RiderArcher(
    projectileCreator: Projectile.Creator,
    private val view: View,
    private val events: RiderEvents,
    private val speedAddition: Double,
    private val stoppingSpeed: Double,
    private val maxSpeed: Double
) : GameObject, RiderEvents by events {

    private var movementPerSecond: IPoint = Point(0)
        set(value) {
            field = (if (value.length > maxSpeed) value.unit * maxSpeed else value)
        }
    private val shooter: Shooter = Shooter.Base(view.pos::copy, projectileCreator)

    override fun update(dt: TimeSpan) {
        /*val vector = movementPerSecond * (dt / 1.seconds)
        view.pos += vector*/
        val events = events()
        events.forEach { event ->
            println(event)
            when (event) {
                is RiderEvent.Move -> movementPerSecond += event.point() * speedAddition
                is RiderEvent.Shoot -> shooter.shoot(event.destination)
            }
        }
        if (events.filterIsInstance<RiderEvent.Move>().isEmpty()) stopping(dt)
    }

    private fun stopping(dt: TimeSpan) {
        if (movementPerSecond.length > 0.1) {
            movementPerSecond -= movementPerSecond.unit * stoppingSpeed * (dt / 1.seconds)
        } else {
            movementPerSecond = Point.Zero
        }
    }
}

interface RiderEvents {

    fun attach(view: View)

    fun events(): List<RiderEvent>
}

sealed class RiderEvent {
    sealed class Move(private val point: IPoint) : RiderEvent() {
        object Up : Move(Point.Up)
        object Down : Move(Point.Down)
        object Left : Move(Point.Left)
        object Right : Move(Point.Right)

        fun point(): IPoint = point.copy()
    }
    class Shoot(val destination: IPoint) : RiderEvent()
}
