package units

import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import kotlin.properties.*

class UnitImpl(
    private val view: View,
    private val hitBoxOffset: IPoint,
    private val maxHealth: Int,
    override val hitRadius: Double,
    health: Int = maxHealth,
    private val healthObserver: HealthObserver = HealthObserver.Unit
) : HittableUnit {
    override var health by Delegates.observable(health) { _, old, new -> healthObserver.onChange(old, new, maxHealth) }
    override val pos: IPoint get() = view.pos + hitBoxOffset

    fun interface HealthObserver {
        fun onChange(oldHealth: Int, newHealth: Int, maxHealth: Int)

        object Unit : HealthObserver {
            override fun onChange(oldHealth: Int, newHealth: Int, maxHealth: Int) {}
        }
    }
}
