package projectiles

import arrow
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import core.*

class ArrowProjectile(
    container: Container,
    manager: ProjectileManager,
    color: RGBA = Colors.BLACK,
    onReachCallback: () -> Unit = {},
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

        private var onReachCallback: () -> Unit = {}
        override fun onReach(block: () -> Unit) = apply {
            onReachCallback = block
        }

        override fun create() = ArrowProjectile(container, manager, color, onReachCallback)
    }
}
