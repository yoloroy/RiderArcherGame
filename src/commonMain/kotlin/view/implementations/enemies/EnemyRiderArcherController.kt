package view.implementations.enemies

import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import game.core.*
import game.core.gameobject.*
import game.core.gameobject.EventsBasedGameObject.Event
import game.core.gameobject.WarriorGameObject.AttackEvent
import game.units.*

class EnemyRiderArcherController(
    private val targetPosProvider: PosProvider,
    private val view: View,
    private val shootingDistance: Double,
    private val vectorAngleDifferenceToStop: Angle,
    private val currentMovement: CurrentMovementPerSecondProvider,
    private val onReachCallback: (destination: IPoint) -> Unit = {}
) : BaseUnit.Controller {

    override val shootPos: IPoint get() = targetPosProvider.pos()

    override fun onReach(destination: IPoint) = onReachCallback(destination)

    override val events: List<Event> get() {
        val isNearToTarget = targetPosProvider.pos().distanceTo(view.pos) < shootingDistance
        return listOf(if (isNearToTarget) AttackEvent else moveOrStop())
    }

    private fun moveOrStop(): Event {
        val angleToTarget = view.angleTo(targetPosProvider.pos())
        val currentAngle = currentMovement.vector().angle
        val anglesDiff = (currentAngle - angleToTarget).absoluteValue
        val moving = currentMovement.vector().length > 0.1
        val pointingInWrongDirection = anglesDiff > vectorAngleDifferenceToStop
        return if (moving && pointingInWrongDirection) {
            StopEvent
        } else {
            MoveEvent(Point(angleToTarget))
        }
    }

    fun interface CurrentMovementPerSecondProvider {
        fun vector(): IPoint
    }
}
