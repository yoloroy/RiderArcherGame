import com.soywiz.korma.geom.IPoint

interface Shooter {

    fun shoot(destination: IPoint)

    class Base(private val posProvider: PosProvider, private val projectileCreator: Projectile.Creator) : Shooter {

        override fun shoot(destination: IPoint) {
            projectileCreator
                .onReach { println("Shot complete") }
                .create()
                .launch(posProvider.get(), destination, 40.0)
        }

        fun interface PosProvider {
            fun get(): IPoint
        }
    }
}
