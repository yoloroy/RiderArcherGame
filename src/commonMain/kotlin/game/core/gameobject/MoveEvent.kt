package game.core.gameobject

import com.soywiz.korma.geom.*

data class MoveEvent(val direction: IPoint) : EventsBasedGameObject.Event
