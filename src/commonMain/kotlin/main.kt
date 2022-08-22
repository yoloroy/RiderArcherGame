import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import core.*
import enemies.*
import player.*
import projectiles.*
import units.rider.*

suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors["#ffffff"]) {
    val sceneContainer = sceneContainer()

    sceneContainer.changeTo({ MyScene() })
}

class MyScene : Scene() {
	override suspend fun SContainer.sceneMain() {
        val projectileManager = BaseProjectileManager()
        val projectileCreator = ArrowProjectile.Creator(this, Colors.BLACK, manager = projectileManager)
        val playerView = solidRect(10, 10, Colors.BLACK) {
            position(this@sceneMain.width / 2, this@sceneMain.height / 2)
            anchor(0.5, 0.5)
        }

        val hitRadius = 10.0
        val playerRiderArcher = RiderArcher(
            playerView,
            PlayerRiderArcherController(Key.W, Key.S, Key.A, Key.D).also { it.attach(this) },
            projectileCreator,
            80.0,
            30.0,
            20.0,
            140.0,
            1.timesPerSecond
        )

        val enemyRiders = listOf(
            solidRect(10, 10, Colors.RED) {
                position(this@sceneMain.width / 4, this@sceneMain.height / 4)
                anchor(0.5, 0.5)
            },
            solidRect(10, 10, Colors.VIOLET) {
                position(this@sceneMain.width / 4 * 3, this@sceneMain.height / 4 * 3)
                anchor(0.5, 0.5)
            }
        ).map { view ->
            RiderArcher(
                view,
                EnemyRiderArcherController(playerView, view, 50.0, hitRadius),
                projectileCreator,
                55.0,
                20.0,
                15.0,
                100.0,
                0.5.timesPerSecond
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
