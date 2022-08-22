package core.gameobject

import com.soywiz.klock.*
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import core.*
import util.*

abstract class WarriorGameObject(
    view: View,
    eventsProvider: EventsProvider,
    projectileCreator: Projectile.Creator
) : EventsBasedGameObject(view, eventsProvider) {

    protected abstract val shootPos: IPoint
    protected abstract val attackFrequency: Frequency
    protected abstract val projectileSpeed: Double

    private var lastShotTime: TimeSpan = DateTime.now().time.encoded

    init {
        projectileCreator.onReach(::onProjectileArrived)
    }

    private val shooter: Shooter = Shooter.Base({ view.pos.copy() }, projectileCreator)

    override fun consumeEvent(dt: TimeSpan, event: Event) {
        val currentTime = DateTime.now().time.encoded
        val calculatedFrequency = (currentTime - lastShotTime).toFrequency()
        if (event is AttackEvent && calculatedFrequency <= attackFrequency) {
            shooter.shoot(shootPos, projectileSpeed)
            lastShotTime = currentTime
        }
    }

    protected abstract fun onProjectileArrived(destination: IPoint)

    object AttackEvent : Event
}
