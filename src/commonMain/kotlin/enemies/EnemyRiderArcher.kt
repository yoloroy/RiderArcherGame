package enemies

import com.soywiz.klock.*
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import core.*
import units.rider.*

class EnemyRiderArcher(
    projectileCreator: Projectile.Creator,
    private val enemyView: View,
    private val view: View,
    private val shootingDistance: Double,
    speedAdditionPerSecond: Double,
    stoppingSpeedPerSecond: Double,
    maxSpeedPerSecond: Double,
    projectileLaunchSpeedPerSecond: Double,
    private val hitRadius: Double,
    private val shootDelay: TimeSpan
) : GameObject, RiderEventsProvider {

    private var lastShotTime: TimeSpan = currentTime()
    private var arrowsDestinations = mutableListOf<IPoint>()

    private val riderArcher = RiderArcher(
        projectileCreator.onReach(::onReach),
        view,
        this,
        speedAdditionPerSecond,
        stoppingSpeedPerSecond,
        maxSpeedPerSecond,
        projectileLaunchSpeedPerSecond
    )

    private fun onReach() {
        if (arrowsDestinations.any() { enemyView.pos.distanceTo(it) < hitRadius }) {
            println("enemy hit")
        } else {
            println("enemy miss")
        }
    }

    override fun update(dt: TimeSpan) = riderArcher.update(dt)

    override val events: List<RiderEvent> get() = mutableListOf<RiderEvent>().apply {
        val distancePoint = enemyView.pos - view.pos

        if (currentTime() >= (lastShotTime + 10.seconds) && distancePoint.length < shootingDistance) {
            lastShotTime = currentTime()
            add(RiderEvent.Shoot(enemyView.pos))
            arrowsDestinations += enemyView.pos.copy()
            return@apply
        }

        if (distancePoint.x > 0) add(RiderEvent.Move.Right)
        if (distancePoint.x < 0) add(RiderEvent.Move.Left)
        if (distancePoint.y > 0) add(RiderEvent.Move.Down)
        if (distancePoint.y < 0) add(RiderEvent.Move.Up)
    }

    private fun currentTime() = DateTime.now().time.encoded
}
