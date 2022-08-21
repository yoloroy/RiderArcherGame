package core

import core.ProjectileManager.ManageableProjectile
import com.soywiz.korge.view.*

class BaseProjectileManager : ProjectileManager {

    private var projectiles: List<ManageableProjectile> = emptyList()

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
