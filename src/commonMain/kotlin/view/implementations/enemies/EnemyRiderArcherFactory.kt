package view.implementations.enemies

import com.soywiz.korge.view.*
import com.soywiz.korio.concurrent.atomic.*
import com.soywiz.korma.geom.*
import game.core.*
import game.units.*
import view.implementations.enemies.EnemyRiderArcherController.CurrentMovementPerSecondProvider

class EnemyRiderArcherFactory(
    private val commonData: CommonData,
    private val targetPosProvider: PosProvider,
    private val projectileCreator: Projectile.Creator,
    private val hitBoxOffset: IPoint,
    private val onDeath: (BaseUnit) -> Unit,
    private val onAttack: (IPoint, Int) -> Unit
) : BaseUnit.Factory<BaseUnit> {

    override fun produce(view: View, data: BaseUnit.Data) = constructor.produce(view, data).also { commonData.enemyRiderArchers += it }

    private val controllerFactory = BaseUnit.Factory<BaseUnit.Controller> { view: View, data: BaseUnit.Data ->
        EnemyRiderArcherController(
            targetPosProvider,
            view,
            data.shootingDistance,
            90.degrees,
            MovementVectorProvider(commonData.nextId),
            onReachCallback = { destination -> onAttack(destination, data.strength) }
        )
    }

    private val hittableUnitFactory = BaseUnit.Factory<HittableUnit> { view: View, data: BaseUnit.Data ->
        UnitImpl(
            view,
            hitBoxOffset,
            data.maxHealth,
            data.hitRadius,
            healthObserver = HealthObserver(commonData.nextId)
        )
    }

    private val constructor = BaseUnit.Constructor(hittableUnitFactory, controllerFactory) { _, _ -> projectileCreator }

    inner class HealthObserver(private val unitId: Int) : UnitImpl.HealthObserver {
        override fun onChange(unit: UnitImpl, oldHealth: Int, newHealth: Int, maxHealth: Int) {
            if (newHealth < 0) onDeath(commonData.enemyRiderArchers[unitId])
        }
    }

    inner class MovementVectorProvider(private val unitId: Int) : CurrentMovementPerSecondProvider {
        override fun vector() = commonData.enemyRiderArchers[unitId].currentMovementVectorPerSecond
    }

    interface CommonData {
        var enemyRiderArchers: List<BaseUnit>

        class Base(override var enemyRiderArchers: List<BaseUnit> = emptyList()) : CommonData

        object Atomic : CommonData {
            override var enemyRiderArchers by KorAtomicRef(emptyList<BaseUnit>())
        }
    }

    private val CommonData.nextId get() = enemyRiderArchers.size // this works for thread safe enemyRiderArchers impl
}
