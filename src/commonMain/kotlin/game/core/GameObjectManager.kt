package game.core

import com.soywiz.korge.view.*
import com.soywiz.korio.lang.*

interface GameObjectManager {

    fun start(mainView: View): Cancellable

    fun onStart(gameObject: ManageableGameObject)

    fun removeGameObject(gameObject: ManageableGameObject)

    interface ManageableGameObject : GameObject {

        fun remove()
    }
}
