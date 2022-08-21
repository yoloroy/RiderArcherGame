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
        val shooterView = solidRect(10, 10, Colors.BLACK) {
            position(this@sceneMain.width / 2, this@sceneMain.height / 2)
            anchor(0.5, 0.5)
        }
        val riderEvents = PlayerRiderEventsProvider(Key.W, Key.S, Key.A, Key.D)
        riderEvents.attach(this)

        val hitRadius = 10.0
        val riderArcher = RiderArcher(
            projectileCreator,
            shooterView,
            riderEvents,
            30.0,
            20.0,
            80.0,
            140.0
        )

        val enemyRiders = listOf(
            EnemyRiderArcher(
                projectileCreator,
                shooterView,
                solidRect(10, 10, Colors.RED) {
                    position(this@sceneMain.width / 4, this@sceneMain.height / 4)
                    anchor(0.5, 0.5)
                },
                50.0,
                20.0,
                15.0,
                55.0,
                100.0,
                hitRadius,
                0.5.seconds
            ),
            EnemyRiderArcher(
                projectileCreator,
                shooterView,
                solidRect(10, 10, Colors.VIOLET) {
                    position(this@sceneMain.width / 4 * 3, this@sceneMain.height / 4 * 3)
                    anchor(0.5, 0.5)
                },
                50.0,
                20.0,
                15.0,
                55.0,
                100.0,
                hitRadius,
                0.5.seconds
            )
        )

        projectileManager.start(this)
        addUpdater { dt ->
            riderArcher.update(dt)
            enemyRiders.forEach {
                it.update(dt)
            }
        }
	}
}
