import com.soywiz.korma.geom.*

interface Projectile {

    fun launch(start: IPoint, destination: IPoint, speedPerSecond: Double)

    interface Creator {

        fun onReach(block: () -> Unit): Creator

        fun create(): Projectile
    }
}
