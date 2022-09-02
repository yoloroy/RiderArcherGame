package game.core

import com.soywiz.korma.geom.*

interface Shooter {

    fun shoot(destination: IPoint, speedPerSeconds: Double)

    class Base(private val posProvider: PosProvider, private val projectileCreator: Projectile.Creator) : Shooter {

        override fun shoot(destination: IPoint, speedPerSeconds: Double) {
            projectileCreator
                .create()
                .launch(posProvider.pos(), destination, speedPerSeconds)
        }
    }
}
