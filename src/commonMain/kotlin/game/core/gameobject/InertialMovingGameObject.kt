package game.core.gameobject

import com.soywiz.klock.*
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import util.*

abstract class InertialMovingGameObject(
    view: View,
    eventsProvider: EventsProvider
) : EventsBasedGameObject(view, eventsProvider) {

    protected abstract val maxMovementPerSecond: Double
    protected abstract val speedAdditionPerSecond: Double
    protected abstract val speedReductionPerSecond: Double
    var movementVectorPerSecond: IPoint = Point(0)
        private set(value) {
            field = value.coerceLengthIn(0.0..maxMovementPerSecond)
        }
    private var hasMoved = false

    override fun update(dt: TimeSpan) {
        hasMoved = false
        super.update(dt)
        view.pos += movementVectorPerSecond * dt.seconds
        view.invalidateMatrix()
        if (!hasMoved) if (movementVectorPerSecond.length > 0.1) {
            movementVectorPerSecond -= movementVectorPerSecond.unit * speedReductionPerSecond * dt.seconds
        } else {
            movementVectorPerSecond = Point.Zero
        }
    }

    override fun consumeEvent(dt: TimeSpan, event: Event) {
        when (event) {
            is MoveEvent -> {
                movementVectorPerSecond += event.direction * speedAdditionPerSecond * dt.seconds
                hasMoved = true
            }
            is StopEvent -> {
                hasMoved = false
            }
        }
    }

    class Static(
        view: View,
        eventsProvider: EventsProvider,
        override val maxMovementPerSecond: Double,
        override val speedReductionPerSecond: Double,
        override val speedAdditionPerSecond: Double
    ) : InertialMovingGameObject(view, eventsProvider)
}
