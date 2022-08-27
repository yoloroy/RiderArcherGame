package units

import com.soywiz.korma.geom.*

interface HittableUnit {
    var health: Int
    val pos: IPoint
    val hitRadius: Double
}

// returns is this hit has killed unit
fun HittableUnit.tryToHit(attackDestination: IPoint, strength: Int): Boolean {
    if (isHit(attackDestination)) health -= strength
    return health < 0
}

fun HittableUnit.isHit(attackDestination: IPoint): Boolean = pos.distanceTo(attackDestination) < hitRadius
