package units

import com.soywiz.korma.geom.*

interface HittableUnit {
    var health: Int
    val pos: IPoint
    val hitRadius: Double
}

fun HittableUnit.tryToHit(attackDestination: IPoint, strength: Int) = if (isHit(attackDestination)) health -= strength else Unit

fun HittableUnit.isHit(attackDestination: IPoint): Boolean = pos.distanceTo(attackDestination) < hitRadius
