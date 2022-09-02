package game.core

import com.soywiz.korge.view.*
import com.soywiz.korio.lang.*

interface ProjectileManager {

    fun start(mainView: View): Cancellable

    fun onLaunch(projectile: ManageableProjectile)

    fun onFinish(projectile: ManageableProjectile)

    interface ManageableProjectile : Projectile, GameObject {

        fun onReach()

        fun clear()
    }
}
