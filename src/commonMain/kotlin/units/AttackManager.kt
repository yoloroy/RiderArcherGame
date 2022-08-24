package units

import com.soywiz.korma.geom.*

class AttackManager(
    private val hittableUnits: List<HittableUnit>
) {
    fun attack(destination: IPoint, strength: Int) = hittableUnits.forEach { it.tryToHit(destination, strength) }
}
