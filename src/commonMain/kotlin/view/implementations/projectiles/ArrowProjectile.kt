package view.implementations.projectiles

import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import game.core.*
import view.graphics_components.*

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

    class Creator(
        private val container: Container,
        private val color: RGBA = Colors.RED,
        private val manager: ProjectileManager
    ) : Projectile.Creator {

        private var onReachCallback: (destination: IPoint) -> Unit = {}
        override fun onReach(block: (destination: IPoint) -> Unit) = apply {
            onReachCallback = block
        }

        override fun create() = ArrowProjectile(container, manager, color, onReachCallback)
    }
}
