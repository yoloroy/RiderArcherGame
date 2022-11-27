package view.implementations.projectiles

import com.soywiz.klock.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import game.core.*
import view.graphics_components.*
import kotlin.math.*
import kotlin.properties.*

class ArrowProjectile(
    container: Container,
    manager: ProjectileManager,
    color: RGBA = Colors.BLACK,
    onReachCallback: (destination: IPoint) -> Unit = {},
) : BaseProjectile(
    container.arrow(color) { size(15, 2) },
    container,
    onReachCallback,
    manager
) {

    companion object {
        const val G = 9.81
        const val minimumScale = 0.5
    }

    private lateinit var canopyShootingMath: ArrowCanopyShootingMath
    private var startScale by Delegates.notNull<Double>()

    override fun launch(start: IPoint, destination: IPoint, speedPerSecond: Double) {
        super.launch(start, destination, speedPerSecond)
        println("launch from $start to $destination")
        canopyShootingMath = ArrowCanopyShootingMath.forLaunch(G, fullDistance, speedPerSecond)
        startScale = view.scale
        view.scale = startScale * minimumScale
    }

    override fun update(dt: TimeSpan) {
        super.update(dt)
        canopyShootingMath.update(dt)
        view.scale = startScale * amplify(canopyShootingMath.projectionRatio)
    }

    private fun amplify(sin: Double) = (sin + 0.3).pow(3).coerceAtLeast(minimumScale)

    class Creator(
        private val container: Container,
        private val color: RGBA = Colors.RED,
        private val manager: ProjectileManager
    ) : Projectile.Creator() {

        override fun create() = ArrowProjectile(container, manager, color) {
            for (callback in onReachCallbacks) {
                callback(it)
            }
        }
    }
}

class ArrowCanopyShootingMath private constructor(
    private var verticalSpeedPerSecond: Double,
    private val gravityAccelerationPerSecond: Double,
    private val horizontalSpeedPerSecond: Double
) {

    companion object {
        fun forLaunch(
            gravityAccelerationPerSecond: Double,
            fullDistance: Double,
            horizontalSpeedPerSecond: Double
        ) = ArrowCanopyShootingMath(
            gravityAccelerationPerSecond * fullDistance / horizontalSpeedPerSecond,
            gravityAccelerationPerSecond,
            horizontalSpeedPerSecond
        )

        const val C = 100
    }

    private var accumulatedAltitude = 0.0

    val projectionRatio get() = sin(angle)

    // why there atan(y * C / x') instead of atan(y' * C / x')? - because that's how it works
    private val angle get() = atan(accumulatedAltitude * C / horizontalSpeedPerSecond).radians

    fun update(dt: TimeSpan) {
        accumulatedAltitude += verticalSpeedPerSecond * dt.seconds
        verticalSpeedPerSecond -= gravityAccelerationPerSecond * dt.seconds
    }
}
