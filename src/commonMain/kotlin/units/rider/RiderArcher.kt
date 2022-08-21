package units.rider

import com.soywiz.klock.*
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import core.*

class RiderArcher(
    projectileCreator: Projectile.Creator,
    private val view: View,
    eventsProvider: RiderEventsProvider,
    private val speedAdditionPerSecond: Double,
    private val stoppingSpeedPerSecond: Double,
    private val maxSpeedPerSecond: Double,
    private val projectileLaunchSpeedPerSecond: Double
) : GameObject, RiderEventsProvider by eventsProvider {

    private var movementPerSecond: IPoint = Point(0)
        set(value) {
            field = (if (value.length > maxSpeedPerSecond) value.unit * maxSpeedPerSecond else value)
        }
    private val shooter: Shooter = Shooter.Base({ view.pos.copy() }, projectileCreator)

    override fun update(dt: TimeSpan) {
        events(dt)
        movement(dt)
    }

    private fun movement(dt: TimeSpan) {
        val vector = movementPerSecond * (dt / 1.seconds)
        view.pos += vector
    }

    private fun events(dt: TimeSpan) {
        events.forEach { event ->
            println(event)
            when (event) {
                is RiderEvent.Move -> movementPerSecond += event.point() * speedAdditionPerSecond * (dt / 1.seconds)
                is RiderEvent.Shoot -> shooter.shoot(event.destination, projectileLaunchSpeedPerSecond)
            }
        }
        if (events.filterIsInstance<RiderEvent.Move>().isEmpty()) stopping(dt)
    }

    private fun stopping(dt: TimeSpan) {
        if (movementPerSecond.length > 0.1) {
            movementPerSecond -= movementPerSecond.unit * stoppingSpeedPerSecond * (dt / 1.seconds)
        } else {
            movementPerSecond = Point.Zero
        }
    }
}
