package enemies

import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import core.gameobject.*
import core.gameobject.EventsBasedGameObject.Event
import core.gameobject.WarriorGameObject.AttackEvent
import units.rider.*

class EnemyRiderArcherController(
    private val targetPosProvider: TargetPosProvider,
    private val view: View,
    private val shootingDistance: Double,
    private val onReachCallback: (destination: IPoint) -> Unit = {}
) : RiderArcher.Controller {

    override val shootPos: IPoint get() = targetPosProvider.pos()

    override fun onReach(destination: IPoint) = onReachCallback(destination)

    override val events: List<Event> get() = listOf(
        if ((targetPosProvider.pos() - view.pos).length < shootingDistance) {
            AttackEvent
        } else {
            MoveEvent(Point(view.angleTo(targetPosProvider.pos())))
        }
    )

    fun interface TargetPosProvider {
        fun pos(): IPoint
    }
}
