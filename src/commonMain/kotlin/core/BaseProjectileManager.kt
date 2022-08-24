package core

import core.ProjectileManager.ManageableProjectile
import com.soywiz.korge.view.*
import com.soywiz.korio.concurrent.atomic.*

class BaseProjectileManager : ProjectileManager {

    private var projectiles by KorAtomicRef(emptyList<ManageableProjectile>())

    override fun start(mainView: View) = mainView.addUpdater { dt ->
        for (projectile in projectiles) {
            projectile.update(dt)
        }
    }

    override fun onLaunch(projectile: ManageableProjectile) {
        projectiles = projectiles + projectile
    }

    override fun onFinish(projectile: ManageableProjectile) {
        projectiles = projectiles - projectile
        projectile.onReach()
        projectile.clear()
    }
}
