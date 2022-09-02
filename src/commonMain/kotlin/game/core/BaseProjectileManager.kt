package game.core

import com.soywiz.korge.view.*
import com.soywiz.korio.concurrent.atomic.*
import game.core.ProjectileManager.ManageableProjectile

class BaseProjectileManager : ProjectileManager {

    private var projectiles by KorAtomicRef(emptySet<ManageableProjectile>())

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
