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

class GameScene(
    private val projectileManager: ProjectileManager = BaseProjectileManager(),
    private val attackManager: AttackManager = AttackManager.Base()
) : Scene(), GameObjectManager, AttackManager by attackManager {

    private val associations = mutableMapOf<View, Pair<ManageableGameObject?, HittableUnit?>>() // TODO refactor
    private var gameObjects by KorAtomicRef(emptySet<GameObject>())
    private var score = 0

	override suspend fun SContainer.sceneMain() {
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
        val views = enemyRidersViews + playerView
        views.forEach { associations[it] = null to null }
        val units = listOf(
            UnitImpl(
                playerView,
                playerView.sizePoint / 2,
                100,
                10.0,
                healthObserver = { _, _, new, max ->
                    playerHealthBar!!.update(new.toDouble() / max)
                    if (new < 0) launch {
                        sceneContainer.changeTo({GameOverScene(score)})
                    }
                }
            ).also { associations[playerView] = associations[playerView]!!.copy(second = it) },
        ) + enemyRidersViews.map { view ->
            UnitImpl(
                view,
                Point.Zero,
                20,
                15.0,
                healthObserver = { unit, _, new, _ -> if (new < 0) removeUnit(unit) }
            ).also { associations[view] = associations[view]!!.copy(second = it) }
        }
        units.forEach(::addUnit)

        val playerStrength = 20
        val enemyStrength = 15

        RiderArcher(
            playerView,
            PlayerRiderArcherController(
                Key.W, Key.S, Key.A, Key.D,
                onReachCallback = { pos -> attack(pos, playerStrength) }
            ).also { it.attach(this) },
            projectileCreator,
            maxMovementPerSecond = 80.0,
            speedAdditionPerSecond = 30.0,
            speedReductionPerSecond = 50.0,
            projectileMovementPerSecond = 140.0,
            attackFrequency = 1.timesPerSecond
        ).also {
            onStart(it)
            associations[playerView] = associations[playerView]!!.copy(first =  it)
        }
        enemyRidersViews.forEach { enemyRiderView ->
            RiderArcher(
                enemyRiderView,
                EnemyRiderArcherController(
                    playerView.asPosProvider(Anchor.CENTER),
                    enemyRiderView,
                    50.0,
                    onReachCallback = { pos -> attack(pos, enemyStrength) }
                ),
                projectileCreator,
                maxMovementPerSecond = 55.0,
                speedAdditionPerSecond = 20.0,
                speedReductionPerSecond = 15.0,
                projectileMovementPerSecond = 100.0,
                attackFrequency = 0.5.timesPerSecond
            ).also {
                onStart(it)
                associations[enemyRiderView] = associations[enemyRiderView]!!.copy(first =  it)
            }
        }

        start(this)
	}

    override fun start(mainView: View): Cancellable = with(mainView) {
        keys {
            down(Key.ESCAPE) {
                sceneContainer.changeTo({MainMenuScene()})
            }
        }

        return Cancellable(listOf(
            projectileManager.start(this),
            addUpdater { dt -> gameObjects.forEach { it.update(dt) } }
        ))
    }

    override fun onStart(gameObject: ManageableGameObject) { gameObjects += gameObject }

    override fun remove(gameObject: ManageableGameObject) {
        gameObjects -= gameObject.also { it.remove() }
    }

    override fun removeUnit(unit: HittableUnit) {
        attackManager.removeUnit(unit)
        val gameObject = associations.toList().find { (_, pair) -> pair.second == unit }!!.second.first!! // TODO
        remove(gameObject)
        score += 1
    }
}
