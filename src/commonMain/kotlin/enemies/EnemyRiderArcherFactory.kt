package enemies

import com.soywiz.korge.view.*
import com.soywiz.korio.concurrent.atomic.*
import com.soywiz.korma.geom.*
import core.*
import enemies.EnemyRiderArcherController.CurrentMovementPerSecondProvider
import units.*
import units.rider.*

class EnemyRiderArcherFactory(
    private val commonData: CommonData,
    private val targetPosProvider: PosProvider,
    private val projectileCreator: Projectile.Creator,
    private val hitBoxOffset: IPoint,
    private val onDeath: (RiderArcher) -> Unit,
    private val onAttack: (IPoint, Int) -> Unit
) : RiderArcher.Factory<RiderArcher> {

    override fun produce(view: View, data: RiderArcher.Data) = constructor.produce(view, data).also { commonData.enemyRiderArchers += it }

    private val controllerFactory = RiderArcher.Factory<RiderArcher.Controller> { view: View, data: RiderArcher.Data ->
        EnemyRiderArcherController(
            targetPosProvider,
            view,
            data.shootingDistance,
            90.degrees,
            MovementVectorProvider(commonData.nextId),
            onReachCallback = { destination -> onAttack(destination, data.strength) }
        )
    }

    private val hittableUnitFactory = RiderArcher.Factory<HittableUnit> { view: View, data: RiderArcher.Data ->
        UnitImpl(
            view,
            hitBoxOffset,
            data.maxHealth,
            data.hitRadius,
            healthObserver = HealthObserver(commonData.nextId)
        )
    }

    private val constructor = RiderArcher.Constructor(hittableUnitFactory, controllerFactory) { _, _ -> projectileCreator }

    inner class HealthObserver(private val unitId: Int) : UnitImpl.HealthObserver {
        override fun onChange(unit: UnitImpl, oldHealth: Int, newHealth: Int, maxHealth: Int) {
            if (newHealth < 0) onDeath(commonData.enemyRiderArchers[unitId])
        }
    }

    inner class MovementVectorProvider(private val unitId: Int) : CurrentMovementPerSecondProvider {
        override fun vector() = commonData.enemyRiderArchers[unitId].currentMovementVectorPerSecond
    }

    interface CommonData {
        var enemyRiderArchers: List<RiderArcher>

        class Base(override var enemyRiderArchers: List<RiderArcher> = emptyList()) : CommonData

        object Atomic : CommonData {
            override var enemyRiderArchers by KorAtomicRef(emptyList<RiderArcher>())
        }
    }

    private val CommonData.nextId get() = enemyRiderArchers.size // this works for thread safe enemyRiderArchers impl
}
