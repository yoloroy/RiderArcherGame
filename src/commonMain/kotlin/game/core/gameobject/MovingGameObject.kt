package game.core.gameobject

import com.soywiz.klock.*
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*

abstract class MovingGameObject(
    view: View,
    eventsProvider: EventsProvider
) : EventsBasedGameObject(view, eventsProvider) {

    protected abstract val movementPerSecond: Double

    override fun consumeEvent(dt: TimeSpan, event: Event) {
        if (event is MoveEvent) {
            view.pos += event.direction * movementPerSecond * dt.seconds
            view.invalidateMatrix()
        }
    }
}

