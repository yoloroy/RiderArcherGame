package game.units

import com.soywiz.korio.concurrent.atomic.*
import com.soywiz.korma.geom.*

interface AttackManager {

    val units: Set<HittableUnit>

    fun attack(destination: IPoint, strength: Int)

    fun addUnit(unit: HittableUnit)

    fun removeUnit(unit: HittableUnit)

    class Base(units: Collection<HittableUnit> = emptySet()) : AttackManager {

        override var units by KorAtomicRef(units.toSet())
            private set

        override fun attack(destination: IPoint, strength: Int) = units.forEach { it.tryToHit(destination, strength) }

        override fun addUnit(unit: HittableUnit) { units += unit }

        override fun removeUnit(unit: HittableUnit) { units -= unit }
    }
}
