package core.gameobject

import com.soywiz.klock.TimeSpan
import com.soywiz.korge.view.View
import com.soywiz.korma.geom.*
import util.coerceLengthIn

abstract class InertialMovingGameObject(
    view: View,
    eventsProvider: EventsProvider
) : EventsBasedGameObject(view, eventsProvider) {

    protected abstract val maxMovementPerSecond: Double
    protected abstract val speedAdditionPerSecond: Double
    protected abstract val speedReductionPerSecond: Double
    private var movementVectorPerSecond: IPoint = Point(0)
        set(value) {
            field = value.coerceLengthIn(0.0..maxMovementPerSecond)
        }
    private var hasMoved = false

    override fun update(dt: TimeSpan) {
        hasMoved = false
        super.update(dt)
        view.pos += movementVectorPerSecond * dt.seconds
        if (!hasMoved) if (movementVectorPerSecond.length > 0.1) {
            movementVectorPerSecond -= movementVectorPerSecond.unit * speedReductionPerSecond * dt.seconds
        } else {
            movementVectorPerSecond = Point.Zero
        }
    }

    override fun consumeEvent(dt: TimeSpan, event: Event) {
        if (event is MoveEvent) {
            movementVectorPerSecond += event.direction * speedAdditionPerSecond * dt.seconds
            hasMoved = true
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
