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
        const val G = 400.0 // todo refactor, move to place where arrow speed is stated
        const val minimumScale = 0.5
    }

    private lateinit var canopyShootingMath: ArrowCanopyShootingMath
    private var startScale by Delegates.notNull<Double>()

    override fun launch(start: IPoint, destination: IPoint, speedPerSecond: Double) {
        super.launch(start, destination, speedPerSecond)
        canopyShootingMath = ArrowCanopyShootingMath.forLaunch(G, fullDistance, speedPerSecond)
        startScale = view.scale
        view.scale = startScale * minimumScale
    }

    override fun update(dt: TimeSpan) {
        super.update(dt)
        canopyShootingMath.update(dt)
        view.scale = startScale * amplify(canopyShootingMath.projectionRatio)
    }

    private fun amplify(ratio: Double) = (ratio * 2).coerceAtLeast(minimumScale)

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
            gravityAccelerationPerSecond * time(fullDistance, horizontalSpeedPerSecond).pow(2),
            gravityAccelerationPerSecond,
            horizontalSpeedPerSecond
        )

        private fun time(distance: Double, speedPerSecond: Double) = distance / speedPerSecond
    }

    val projectionRatio get() = sin(angleInRadians)

    private val angleInRadians get() = atan(verticalSpeedPerSecond / horizontalSpeedPerSecond)

    fun update(dt: TimeSpan) {
        verticalSpeedPerSecond -= gravityAccelerationPerSecond * dt.seconds
    }
}
