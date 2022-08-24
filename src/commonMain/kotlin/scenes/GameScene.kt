package scenes

import com.soywiz.klock.timesPerSecond
import com.soywiz.korev.Key
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Point
import core.*
import enemies.EnemyRiderArcherController
import player.PlayerRiderArcherController
import projectiles.ArrowProjectile
import ui.HealthBarViewHolder
import ui.healthBar
import units.AttackManager
import units.UnitImpl
import units.rider.RiderArcher
import util.sizePoint

class GameScene : Scene() {
	override suspend fun SContainer.sceneMain() {
        val projectileManager = BaseProjectileManager()
        val projectileCreator = ArrowProjectile.Creator(this, Colors.BLACK, manager = projectileManager)
        var playerHealthBar: HealthBarViewHolder? = null // todo fix nullability
        val playerView = container {
            position(this@sceneMain.width / 2, this@sceneMain.height / 2)

            solidRect(10, 10, Colors.BLACK) { position(1.0, 8.0) } // archer with horse will be here
            playerHealthBar = healthBar(10.0, 4.0, Colors.BLACK, Colors.WHITE, Colors.RED, 1.0)
        }
        val enemyRidersViews = listOf(
            solidRect(10, 10, Colors.RED) {
                position(this@sceneMain.width / 4, this@sceneMain.height / 4)
                anchor(0.5, 0.5)
            },
            solidRect(10, 10, Colors.VIOLET) {
                position(this@sceneMain.width / 4 * 3, this@sceneMain.height / 4 * 3)
                anchor(0.5, 0.5)
            }
        )
        val units = listOf(
            UnitImpl(
                playerView,
                playerView.sizePoint / 2,
                100,
                10.0,
                healthObserver = { _, new, max -> playerHealthBar!!.update(new.toDouble() / max) }
            ),
        ) + enemyRidersViews.map { view ->
            UnitImpl(view, Point.Zero, 20, 15.0)
        }
        val attackManager = AttackManager(units)

        val playerStrength = 20
        val enemyStrength = 15

        val playerRiderArcher = RiderArcher(
            playerView,
            PlayerRiderArcherController(
                Key.W,
                Key.S,
                Key.A,
                Key.D,
                onReachCallback = { pos -> attackManager.attack(pos, playerStrength) }
            ).also { it.attach(this) },
            projectileCreator,
            maxMovementPerSecond = 80.0,
            speedAdditionPerSecond = 30.0,
            speedReductionPerSecond = 50.0,
            projectileMovementPerSecond = 140.0,
            attackFrequency = 1.timesPerSecond
        )
        val enemyRiders = enemyRidersViews.map { enemyRiderView ->
            RiderArcher(
                enemyRiderView,
                EnemyRiderArcherController(
                    PosProvider.ofViewCenter(playerView),
                    enemyRiderView,
                    50.0,
                    onReachCallback = { pos -> attackManager.attack(pos, enemyStrength) }
                ),
                projectileCreator,
                maxMovementPerSecond = 55.0,
                speedAdditionPerSecond = 20.0,
                speedReductionPerSecond = 15.0,
                projectileMovementPerSecond = 100.0,
                attackFrequency = 0.5.timesPerSecond
            )
        }

        projectileManager.start(this)
        addUpdater { dt ->
            playerRiderArcher.update(dt)
            enemyRiders.forEach {
                it.update(dt)
            }
        }
        keys {
            down(Key.ESCAPE) {
                sceneContainer.changeTo({MainMenuScene()})
            }
        }
	}
}
