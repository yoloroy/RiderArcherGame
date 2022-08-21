import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA

class Line1x5Projectile(
    container: Container,
    color: RGBA,
    onReachCallback: () -> Unit,
    manager: ProjectileManager
) : BaseProjectile(
    container.solidRect(5.0, 1.0, color),
    container,
    onReachCallback = onReachCallback,
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

        override fun create() = Line1x5Projectile(container, color, onReachCallback, manager)
    }
}
