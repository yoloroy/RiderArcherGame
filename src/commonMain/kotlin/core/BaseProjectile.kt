package core

import core.ProjectileManager.ManageableProjectile
import com.soywiz.klock.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tween.*
import com.soywiz.korio.async.*
import com.soywiz.korma.geom.*
import kotlinx.coroutines.*

open class BaseProjectile(
    private val view: View,
    private val container: Container,
    private val onReachCallback: (destination: IPoint) -> Unit,
    private val manager: ProjectileManager
) : ManageableProjectile {

    private lateinit var start: IPoint
    private lateinit var destination: IPoint
    private lateinit var speedVectorPerSecond: IPoint

    override fun launch(start: IPoint, destination: IPoint, speedPerSecond: Double) {
        this.start = start.copy()
        this.destination = destination.copy()
        this.speedVectorPerSecond = (destination - start).unit * speedPerSecond
        launchImmediately(Dispatchers.Default) {
            view.pos = start
            view.invalidateMatrix()
            view.rotateTo(start.angleTo(destination), TimeSpan.ZERO)
        }
        manager.onLaunch(this)
    }

    override fun update(dt: TimeSpan) {
        val vector = speedVectorPerSecond * (dt / 1.seconds)
        view.pos += vector
        view.invalidateMatrix()
        if (destination.distanceTo(view.pos) <= (vector.length * 2)) {
            manager.onFinish(this)
        }
    }

    override fun onReach() = onReachCallback(destination)

    override fun clear() {
        container.removeChild(view)
    }
}
