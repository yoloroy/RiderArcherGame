import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import core.*
import enemies.*
import player.*
import projectiles.*
import ui.*
import units.*
import units.rider.*
import util.*

suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors.WHEAT) {
    val sceneContainer = sceneContainer()

    sceneContainer.changeTo({ MyScene() })
}

class MyScene : Scene() {
	override suspend fun SContainer.sceneMain() {
        val projectileManager = BaseProjectileManager()
        val projectileCreator = ArrowProjectile.Creator(this, Colors.BLACK, manager = projectileManager)
        var playerHealthBar: HealthBarViewHolder? = null // todo fix nullability
        val playerView = container {
            position(this@sceneMain.width / 2, this@sceneMain.height / 2)

            solidRect(10, 10, Colors.BLACK) { position(1.0, 8.0) } // archer with horse will be here
            playerHealthBar = healthBar(10.0, 4.0, Colors.BLACK, Colors.WHITE, Colors.RED, 1.0)
        }
        val playerPosGetter = { playerView.pos + playerView.sizePoint / 2 }
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
            PlayerRiderArcherController(Key.W, Key.S, Key.A, Key.D) { attackManager.attack(it, playerStrength) }.also { it.attach(this) },
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
                EnemyRiderArcherController(playerPosGetter, enemyRiderView, 50.0) { attackManager.attack(it, enemyStrength) },
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
	}
}
