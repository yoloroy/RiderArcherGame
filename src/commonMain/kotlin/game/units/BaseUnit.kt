package game.units

import com.soywiz.klock.*
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import game.core.*
import game.core.GameObjectManager.ManageableGameObject
import game.core.gameobject.*
import game.core.gameobject.EventsBasedGameObject.EventsProvider

class BaseUnit(
    private val view: View,
    hittableUnit: HittableUnit,
    controller: Controller,
    projectileCreator: Projectile.Creator,
    maxMovementPerSecond: Double,
    speedAdditionPerSecond: Double,
    speedReductionPerSecond: Double,
    projectileMovementPerSecond: Double,
    attackFrequency: Frequency
) : ManageableGameObject, HittableUnit by hittableUnit {

    val currentMovementVectorPerSecond get() = horseRiding.movementVectorPerSecond

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

    class Constructor(
        private val hittableUnitFactory: Factory<HittableUnit>,
        private val controllerFactory: Factory<Controller>,
        private val projectileCreatorFactory: Factory<Projectile.Creator>
    ): Factory<BaseUnit> {
        override fun produce(view: View, data: Data) = BaseUnit(
            view,
            hittableUnitFactory.produce(view, data),
            controllerFactory.produce(view, data),
            projectileCreatorFactory.produce(view, data),
            data.maxMovementPerSecond,
            data.speedAdditionPerSecond,
            data.speedReductionPerSecond,
            data.projectileMovementPerSecond,
            data.attackFrequency,
        )
    }

    data class Data(
        val maxHealth: Int,
        val hitRadius: Double,
        val strength: Int,
        val shootingDistance: Double,
        val maxMovementPerSecond: Double,
        val speedAdditionPerSecond: Double,
        val speedReductionPerSecond: Double,
        val projectileMovementPerSecond: Double,
        val attackFrequency: Frequency
    )

    fun interface Factory<T> {
        fun produce(view: View, data: Data): T
    }
}
