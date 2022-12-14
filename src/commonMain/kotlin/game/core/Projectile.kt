package game.core

import com.soywiz.korma.geom.*

interface Projectile {

    fun launch(start: IPoint, destination: IPoint, speedPerSecond: Double)

    abstract class Creator {

        protected var onReachCallbacks = listOf<(destination: IPoint) -> Unit>()

        fun appendOnReach(block: (destination: IPoint) -> Unit) = apply {
            onReachCallbacks += block
        }

        abstract fun create(): Projectile
    }
}
