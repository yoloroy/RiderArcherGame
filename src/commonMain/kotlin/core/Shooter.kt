package core

import com.soywiz.korma.geom.IPoint

interface Shooter {

    fun shoot(destination: IPoint, speedPerSeconds: Double)

    class Base(private val posProvider: PosProvider, private val projectileCreator: Projectile.Creator) : Shooter {

        override fun shoot(destination: IPoint, speedPerSeconds: Double) {
            projectileCreator
                .onReach { println("Shot complete") }
                .create()
                .launch(posProvider.get(), destination, speedPerSeconds)
        }

        fun interface PosProvider {
            fun get(): IPoint
        }
    }
}
