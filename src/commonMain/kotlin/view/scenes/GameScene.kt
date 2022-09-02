package view.scenes

import SessionData.controls
import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korio.concurrent.atomic.*
import com.soywiz.korio.lang.*
import com.soywiz.korma.geom.*
import game.core.*
import game.core.GameObjectManager.ManageableGameObject
import game.units.*
import game.units.rider.*
import kotlinx.coroutines.*
import util.*
import view.graphics_components.*
import view.implementations.enemies.*
import view.implementations.player.*
import view.implementations.projectiles.*
import kotlin.random.*

class GameScene(
    private val projectileManager: ProjectileManager = BaseProjectileManager(),
    private val attackManager: AttackManager = AttackManager.Base()
) : Scene(), GameObjectManager, AttackManager by attackManager {

    private var gameObjects by KorAtomicRef(emptySet<GameObject>())
    private var score = 0
    private val riderArcherSize = Size(10, 10)
    private lateinit var playerPosProvider: PosProvider
    private lateinit var arrowsCreator: Projectile.Creator

    private val enemyFactoryCommonData = EnemyRiderArcherFactory.CommonData.Atomic
    private val enemyFactory: RiderArcher.Factory<RiderArcher> by lazy {
        EnemyRiderArcherFactory(
            enemyFactoryCommonData,
            playerPosProvider,
            arrowsCreator,
            riderArcherSize.p / 2,
            ::remove,
            ::attack
        )
    }

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
                controls.up,
                controls.down,
                controls.left,
                controls.right,
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
            enemyRiderArcher()
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
        root.enemyRiderArcher()
        if (Random.nextDouble(0.0, 1.0) > .85) { // 15% chance to spawn new enemy
            root.enemyRiderArcher()
        }
        score += 1
        if (units.size == 1) launch {
            sceneContainer.changeTo({MainMenuScene(score)})
        }
    }

    private fun Container.enemyRiderArcher(
        initialPos: IPoint = randomEnemyPos(),
        characteristics: RiderArcher.Data = LevelData.enemyCharacteristics,
    ): RiderArcher {
        val view = enemyRiderArcherView(initialPos)
        return enemyFactory.produce(view, characteristics).also(::add)
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

object LevelData {
    val enemyCharacteristics = RiderArcher.Data(
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
