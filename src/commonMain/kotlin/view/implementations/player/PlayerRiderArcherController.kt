package view.implementations.player

import com.soywiz.korev.*
import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import game.core.gameobject.*
import game.core.gameobject.EventsBasedGameObject.Event
import game.core.gameobject.WarriorGameObject.AttackEvent
import game.units.*

class PlayerRiderArcherController(
    private val upKey: Key,
    private val downKey: Key,
    private val leftKey: Key,
    private val rightKey: Key,
    private val onReachCallback: (destination: IPoint) -> Unit = {}
) : BaseUnit.Controller {

    private var up = false
    private var down = false
    private var left = false
    private var right = false
    private var shoot: IPoint? = null
    override val shootPos: IPoint get() = shoot!!

    fun attach(view: View) {
        with(view) {
            mouse {
                down { shoot = it.currentPosStage }
                up { shoot = null }
            }
            keys {
                down(upKey) { up = true }
                up(upKey) { up = false }
                down(downKey) { down = true }
                up(downKey) { down = false }
                down(leftKey) { left = true }
                up(leftKey) { left = false }
                down(rightKey) { right = true }
                up(rightKey) { right = false }
            }
        }
    }

    override fun onReach(destination: IPoint) = onReachCallback(destination)

    override val events get() = mutableListOf<Event>().apply {
        if (up) add(moveUp)
        if (down) add(moveDown)
        if (left) add(moveLeft)
        if (right) add(moveRight)
        shoot?.let {
            add(AttackEvent)
        }
    }

    private val moveUp get() = MoveEvent(Point.Up)
    private val moveDown get() = MoveEvent(Point.Down)
    private val moveLeft get() = MoveEvent(Point.Left)
    private val moveRight get() = MoveEvent(Point.Right)
}
