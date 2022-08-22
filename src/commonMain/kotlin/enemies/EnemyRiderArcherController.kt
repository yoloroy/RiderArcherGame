package enemies

import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import core.gameobject.*
import core.gameobject.EventsBasedGameObject.Event
import core.gameobject.WarriorGameObject.AttackEvent
import units.rider.*

class EnemyRiderArcherController(
    private val target: View,
    private val view: View,
    private val shootingDistance: Double,
    private val hitRadius: Double
) : RiderArcher.Controller {

    override val shootPos: IPoint get() = target.pos

    override fun onReach(destination: IPoint) {
        if (target.distanceTo(destination) < hitRadius) {
            println("enemy hit")
        } else {
            println("enemy miss")
        }
    }

    override val events: List<Event> get() = listOf(
        if ((target.pos - view.pos).length < shootingDistance) {
            AttackEvent
        } else {
            MoveEvent(Point(view.angleTo(target)))
        }
    )
}
