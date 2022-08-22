package core.gameobject

import com.soywiz.klock.*
import com.soywiz.korge.view.*
import core.*

abstract class EventsBasedGameObject(
    protected val view: View,
    private val eventsProvider: EventsProvider
) : GameObject {

    override fun update(dt: TimeSpan) {
        eventsProvider.events.forEach {
            consumeEvent(dt, it)
        }
    }

    protected abstract fun consumeEvent(dt: TimeSpan, event: Event)

    interface EventsProvider {
        val events: List<Event>
    }
    interface Event
}

