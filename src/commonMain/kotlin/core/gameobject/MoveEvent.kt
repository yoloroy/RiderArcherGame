package core.gameobject

import com.soywiz.korma.geom.IPoint

data class MoveEvent(val direction: IPoint) : EventsBasedGameObject.Event
