package game.core

import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import util.*

fun interface PosProvider {
    fun pos(): IPoint

    companion object {
        fun ofViewCenter(view: View) = PosProvider { view.pos + view.sizePoint / 2 }
    }
}
