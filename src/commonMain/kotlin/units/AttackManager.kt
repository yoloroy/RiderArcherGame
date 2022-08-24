package units

import com.soywiz.korio.concurrent.atomic.*
import com.soywiz.korma.geom.*

class AttackManager(
    units: List<HittableUnit>
) {
    var units by KorAtomicRef(units)
    fun attack(destination: IPoint, strength: Int) = units.forEach { it.tryToHit(destination, strength) }
}
