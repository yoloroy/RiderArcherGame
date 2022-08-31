package scenes

import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korio.concurrent.atomic.*
import com.soywiz.korio.lang.*
import com.soywiz.korma.geom.*
import core.*
import core.GameObjectManager.ManageableGameObject
import enemies.*
import kotlinx.coroutines.*
import player.*
import projectiles.*
import ui.*
import units.*
import units.rider.*
import util.*
import kotlin.random.*

class GameScene(
    private val projectileManager: ProjectileManager = BaseProjectileManager(),
    private val attackManager: AttackManager = AttackManager.Base()
) : Scene(), GameObjectManager, AttackManager by attackManager {

    private var gameObjects by KorAtomicRef(emptySet<GameObject>())
    private var score = 0
    private lateinit var playerPosProvider: PosProvider
    private lateinit var arrowsCreator: Projectile.Creator

	override suspend fun SContainer.sceneMain() {
        arrowsCreator = ArrowProjectile.Creator(this, Colors.BLACK, manager = projectileManager)
        var playerHealthBar: HealthBarViewHolder? = null // todo fix nullability
        val playerView = container {
            position(this@sceneMain.width / 2, this@sceneMain.height / 2)

            solidRect(10, 10, Colors.BLACK) { position(1.0, 8.0) } // archer with horse will be here
            playerHealthBar = healthBar(10.0, 4.0, Colors.BLACK, Colors.WHITE, Colors.RED, 1.0)
        }
        playerPosProvider = playerView.asPosProvider(Anchor.CENTER)

        val playerStrength = 20

        RiderArcher(
            playerView,
            UnitImpl(
                playerView,
                playerView.sizePoint / 2,
                100,
                10.0,
                healthObserver = { _, _, new, max ->
                    playerHealthBar!!.update(new.toDouble() / max)
                    if (new < 0) launch {
                        sceneContainer.changeTo({MainMenuScene(score)})
                    }
                }
            ),
            PlayerRiderArcherController(
                Key.W, Key.S, Key.A, Key.D,
                onReachCallback = { pos -> attack(pos, playerStrength) }
            ).also { it.attach(this) },
            arrowsCreator,
            maxMovementPerSecond = 80.0,
            speedAdditionPerSecond = 30.0,
            speedReductionPerSecond = 50.0,
            projectileMovementPerSecond = 140.0,
            attackFrequency = 1.timesPerSecond
        ).also {
            add(it)
        }

        repeat(4) {
            enemyRiderArcher(arrowsCreator)
        }

        start(this)
	}

    private fun <T> remove(gameObject: T) where T : ManageableGameObject, T : HittableUnit {
        removeGameObject(gameObject)
        removeUnit(gameObject)
    }

    private fun <T> add(gameObject: T) where T : ManageableGameObject, T : HittableUnit {
        onStart(gameObject)
        addUnit(gameObject)
    }

    override fun start(mainView: View): Cancellable = with(mainView) {
        keys {
            down(Key.ESCAPE) {
                sceneContainer.changeTo({MainMenuScene(score)})
            }
        }

        return Cancellable(listOf(
            projectileManager.start(this),
            addUpdater { dt -> gameObjects.forEach { it.update(dt) } }
        ))
    }

    override fun onStart(gameObject: ManageableGameObject) { gameObjects += gameObject }

    override fun removeGameObject(gameObject: ManageableGameObject) {
        gameObjects -= gameObject.also { it.remove() }
    }

    override fun removeUnit(unit: HittableUnit) {
        attackManager.removeUnit(unit)
        root.enemyRiderArcher(arrowsCreator)
        if (Random.nextDouble(0.0, 1.0) > .85) { // 15% chance to spawn new enemy
            root.enemyRiderArcher(arrowsCreator)
        }
        score += 1
        if (units.size == 1) launch {
            sceneContainer.changeTo({MainMenuScene(score)})
        }
    }

    private var enemyRiderArchers by KorAtomicRef(listOf<RiderArcher>())
    private fun Container.enemyRiderArcher(
        projectileCreator: Projectile.Creator,
        initialPos: IPoint = randomEnemyPos(),
        characteristics: ArcherRiderCharacteristics = ArcherRiderCharacteristics.enemy(),
    ): RiderArcher {
        val view = enemyRiderArcherView(initialPos)
        val i = enemyRiderArchers.size
        return RiderArcher(
            view,
            UnitImpl(
                view,
                Anchor.CENTER.toPoint(),
                characteristics.maxHealth,
                characteristics.hitRadius,
                healthObserver = { _, _, new, _ -> if (new < 0) remove(enemyRiderArchers[i]) }
            ),
            EnemyRiderArcherController(
                playerPosProvider,
                view,
                characteristics.shootingDistance,
                onReachCallback = { pos -> attack(pos, characteristics.strength) }
            ),
            projectileCreator,
            characteristics.maxMovementPerSecond,
            characteristics.speedAdditionPerSecond,
            characteristics.speedReductionPerSecond,
            characteristics.projectileMovementPerSecond,
            characteristics.attackFrequency
        ).also {
            enemyRiderArchers += it
            add(it)
        }
    }

    private val enemyColors = listOf(Colors.RED, Colors.DARKRED, Colors.VIOLET, Colors.DARKVIOLET, Colors.BLUEVIOLET, Colors.CHOCOLATE)
    private fun Container.enemyRiderArcherView(pos: IPoint) = solidRect(10, 10, enemyColors.random()) {
        position(pos.x, pos.y)
    }

    private fun Container.randomEnemyPos(): IPoint {
        val xRange = (width / 4 * 1)..(width / 4 * 3)
        val yRange = (height / 4 * 1)..(height / 4 * 3)
        val sides = listOf(Point.Right, Point.Down)
        return sides.random() * Point(xRange.random(), yRange.random())
    }
}

data class ArcherRiderCharacteristics(
    val maxHealth: Int,
    val hitRadius: Double,
    val strength: Int,
    val shootingDistance: Double,
    val maxMovementPerSecond: Double,
    val speedAdditionPerSecond: Double,
    val speedReductionPerSecond: Double,
    val projectileMovementPerSecond: Double,
    val attackFrequency: Frequency
) {
    companion object {
        fun enemy() = ArcherRiderCharacteristics(
            maxHealth = 20,
            hitRadius = 15.0,
            strength = 20,
            shootingDistance = 50.0,
            maxMovementPerSecond = 55.0,
            speedAdditionPerSecond = 20.0,
            speedReductionPerSecond = 15.0,
            projectileMovementPerSecond = 100.0,
            attackFrequency = 0.5.timesPerSecond
        )
    }
}
