import com.soywiz.korev.*
import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*

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
        }
        val riderEvents = RiderEventsImpl(Key.W, Key.S, Key.A, Key.D)
        riderEvents.attach(this)

        val riderArcher = RiderArcher(
            projectileCreator,
            shooterView,
            riderEvents,
            3.0,
            2.0,
            6.0
        )

        projectileManager.start(this)
        addUpdater { dt ->
            riderArcher.update(dt)
        }
	}
}
