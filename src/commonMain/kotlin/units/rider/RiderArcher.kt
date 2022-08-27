package units.rider

import com.soywiz.klock.*
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import core.*
import core.GameObjectManager.ManageableGameObject
import core.gameobject.*
import core.gameobject.EventsBasedGameObject.EventsProvider

class RiderArcher(
    private val view: View,
    controller: Controller,
    projectileCreator: Projectile.Creator,
    maxMovementPerSecond: Double,
    speedAdditionPerSecond: Double,
    speedReductionPerSecond: Double,
    projectileMovementPerSecond: Double,
    attackFrequency: Frequency
) : ManageableGameObject {

    // region components
    private val horseRiding = InertialMovingGameObject.Static(view, controller, maxMovementPerSecond, speedReductionPerSecond, speedAdditionPerSecond)
    private val shooting = ShootingComponent(view, attackFrequency, projectileMovementPerSecond, projectileCreator, controller)
    private val components = listOf(horseRiding, shooting)
    // endregion

    override fun update(dt: TimeSpan) = components.forEach { it.update(dt) }

    override fun remove() {
        view.removeFromParent()
    }

    inner class ShootingComponent(
        view: View,
        override val attackFrequency: Frequency,
        override val projectileSpeed: Double,
        projectileCreator: Projectile.Creator,
        private val controller: Controller
    ) : WarriorGameObject(view, controller, projectileCreator) {
        override val shootPos: IPoint get() = controller.shootPos
        override fun onProjectileArrived(destination: IPoint) = controller.onReach(destination)
    }

    interface Controller : EventsProvider {
        val shootPos: IPoint

        fun onReach(destination: IPoint)
    }
}
