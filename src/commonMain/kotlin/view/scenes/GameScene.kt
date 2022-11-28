package view.scenes

import Controls
import SessionData
import com.soywiz.klock.*
import com.soywiz.korev.*
import com.soywiz.korge.box2d.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.concurrent.atomic.*
import com.soywiz.korio.file.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.lang.*
import com.soywiz.korma.geom.*
import game.core.*
import game.core.GameObjectManager.ManageableGameObject
import game.units.*
import org.jbox2d.collision.shapes.*
import org.jbox2d.common.*
import org.jbox2d.dynamics.*
import util.*
import view.graphics_components.*
import view.implementations.enemies.*
import view.implementations.player.*
import view.implementations.projectiles.*
import kotlin.random.*

class GameScene(
    private val sessionData: SessionData,
    returnToMenu: ReturnToMenu,
    private val projectileManager: ProjectileManager = BaseProjectileManager(),
    private val attackManager: AttackManager = AttackManager.Base()
) : Scene(),
    GameObjectManager,
    AttackManager by attackManager,
    ReturnToMenu by returnToMenu {

    private var gameObjects by KorAtomicRef(emptySet<GameObject>())
    private var score = 0
    private val riderArcherSize = Size(25, 25)
    private val arrowReachSpriteSize = Size(20, 20)
    private val arrowReachingFrameDuration = 0.2.seconds
    private lateinit var playerPosProvider: PosProvider
    private lateinit var arrowsCreator: Projectile.Creator

    private val enemyFactoryCommonData = EnemyRiderArcherFactory.CommonData.Atomic
    private val enemyFactory: BaseUnit.Factory<BaseUnit> by lazy {
        EnemyRiderArcherFactory(
            enemyFactoryCommonData,
            playerPosProvider,
            arrowsCreator,
            riderArcherSize.p / 2,
            ::remove,
            ::attack
        )
    }

    private lateinit var controls: Controls

    private lateinit var riderArcherBitmap: Bitmap
    private lateinit var enemyRiderArchersBitmaps: List<Bitmap>
    private lateinit var arrowReachingBitmaps: List<Bitmap>

    override suspend fun SContainer.sceneInit() {
        controls = sessionData.loadControls()

        arrowReachingBitmaps = resourcesVfs["arrow_reach_frames"].listSimple()
            .filter { it.baseName.matches("\\d*.png".toRegex()) }
            .sortedBy { it.baseName }
            .map {
                it.readBitmap().resized(
                    arrowReachSpriteSize.width.toInt(),
                    arrowReachSpriteSize.height.toInt(),
                    ScaleMode.FILL,
                    Anchor.CENTER
                )
            }

        riderArcherBitmap = resourcesVfs["riderArcher.png"].readBitmap()
            .resized(riderArcherSize.width.toInt(), riderArcherSize.height.toInt(), ScaleMode.FILL, Anchor.TOP_LEFT)

        // TODO refactor
        val enemyRiderArchersColors = listOf(Colors.RED, Colors.DARKRED, Colors.VIOLET, Colors.DARKVIOLET, Colors.BLUEVIOLET, Colors.CHOCOLATE)
        enemyRiderArchersBitmaps = enemyRiderArchersColors
            .map { newColor ->
                riderArcherBitmap.clone().apply {
                    forEach { _, x, y ->
                        val color = getRgba(x, y)
                        setRgba(x, y, newColor.withA(color.a))
                    }
                }
            }
    }

	override suspend fun SContainer.sceneMain() {
        getOrCreateBox2dWorld().world.apply { // TODO
            gravity = Vec2(0f, 0f)
            customScale = 1.0
        }

        arrowsCreator = ArrowProjectile
            .Creator(this, Colors.BLACK, manager = projectileManager)
            .onReach { destination ->
                val reachingImg = image(arrowReachingBitmaps.first()) {
                    position(destination)
                }
                launch {
                    for (frame in arrowReachingBitmaps.drop(1)) {
                        delay(arrowReachingFrameDuration)
                        reachingImg.bitmap = frame.slice()
                    }
                    delay(arrowReachingFrameDuration)
                    reachingImg.removeFromParent()
                }
            }

        var playerHealthBar: HealthBarViewHolder? = null // todo fix nullability
        val playerView = container {
            position(this@sceneMain.width / 2, this@sceneMain.height / 2)

            image(riderArcherBitmap) {
                size(riderArcherSize.width, riderArcherSize.height)
                position(1.0, 8.0)
            }
            playerHealthBar = healthBar(riderArcherSize.width, 4.0, Colors.BLACK, Colors.WHITE, Colors.RED, 1.0)
        }.registerBodyWithFixture(
            type = BodyType.DYNAMIC,
            shape = CircleShape(8),
            density = 1f
        )
        playerPosProvider = playerView.asPosProvider(Anchor.CENTER)

        val playerStrength = 20

        BaseUnit(
            playerView,
            UnitImpl(
                playerView,
                playerView.sizePoint / 2,
                100,
                15.0,
                healthObserver = { _, _, new, max ->
                    playerHealthBar!!.update(new.toDouble() / max)
                    if (new < 0) sceneContainer.launchReturnToMenu(score)
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
            maxMovementPerSecond = 65.0,
            speedAdditionPerSecond = 30.0,
            speedReductionPerSecond = 50.0,
            projectileMovementPerSecond = 150.0,
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
                sceneContainer.launchReturnToMenu(score)
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
        sceneView.enemyRiderArcher()
        if (Random.nextDouble(0.0, 1.0) > .85) { // 15% chance to spawn new enemy
            sceneView.enemyRiderArcher()
        }
        score += 1
        if (units.size == 1) sceneContainer.launchReturnToMenu(score)
    }

    private fun Container.enemyRiderArcher(
        initialPos: IPoint = randomEnemyPos(),
        characteristics: BaseUnit.Data = LevelData.enemyCharacteristics,
    ): BaseUnit {
        val view = enemyRiderArcherView(initialPos)
        return enemyFactory.produce(view, characteristics).also(::add)
    }

    private fun Container.enemyRiderArcherView(pos: IPoint): View {
        return image(enemyRiderArchersBitmaps.random()) {
            size(riderArcherSize.width, riderArcherSize.height)
            position(pos.x, pos.y)
        }.registerBodyWithFixture(
            type = BodyType.DYNAMIC,
            shape = CircleShape(riderArcherSize.p.length + 2),
            density = 1f
        )
    }

    private fun Container.randomEnemyPos(): IPoint {
        val xRange = (width / 4 * 1)..(width / 4 * 3)
        val yRange = (height / 4 * 1)..(height / 4 * 3)
        val sides = listOf(Point.Right, Point.Down)
        return sides.random() * Point(xRange.random(), yRange.random())
    }
}

object LevelData {
    val enemyCharacteristics = BaseUnit.Data(
        maxHealth = 20,
        hitRadius = 15.0,
        strength = 20,
        shootingDistance = 75.0,
        maxMovementPerSecond = 55.0,
        speedAdditionPerSecond = 25.0,
        speedReductionPerSecond = 45.0,
        projectileMovementPerSecond = 150.0,
        attackFrequency = 0.5.timesPerSecond
    )
}
