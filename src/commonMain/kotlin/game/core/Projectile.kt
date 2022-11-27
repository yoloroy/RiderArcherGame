package game.core

import com.soywiz.korma.geom.*

interface Projectile {

    fun launch(start: IPoint, destination: IPoint, speedPerSecond: Double)

    abstract class Creator {

        protected val onReachCallbacks = mutableListOf<(destination: IPoint) -> Unit>()

        fun onReach(block: (destination: IPoint) -> Unit) = apply {
            onReachCallbacks += block
        }

        abstract fun create(): Projectile
    }
}
