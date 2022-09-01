package enemies

import com.soywiz.korge.view.*
import com.soywiz.korio.concurrent.atomic.*
import com.soywiz.korma.geom.*
import core.*
import units.*
import units.rider.*
import util.*

class EnemyRiderArcherFactory(
    private val commonData: CommonData,
    @Suppress("unused") private val projectileCreator: Projectile.Creator,
    private val hitBoxOffset: IPoint,
    private val onDeath: (RiderArcher) -> Unit,
    private val onAttack: (IPoint, Int) -> Unit
) : RiderArcher.Factory<RiderArcher> {

    override fun produce(view: View, data: RiderArcher.Data) = constructor.produce(view, data)

    private val controllerFactory = RiderArcher.Factory<RiderArcher.Controller> { view: View, data: RiderArcher.Data ->
        EnemyRiderArcherController(
            view.asPosProvider(Anchor.CENTER),
            view,
            data.shootingDistance,
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

    interface CommonData {
        var enemyRiderArchers: List<RiderArcher>

        class Base(override var enemyRiderArchers: List<RiderArcher> = emptyList()) : CommonData

        object Atomic : CommonData {
            override var enemyRiderArchers by KorAtomicRef(emptyList<RiderArcher>())
        }
    }

    private val CommonData.nextId get() = enemyRiderArchers.size // this works for thread safe enemyRiderArchers impl
}
