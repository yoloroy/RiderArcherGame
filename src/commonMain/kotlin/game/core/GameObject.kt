package game.core

import com.soywiz.klock.*

interface GameObject {

    fun update(dt: TimeSpan)
}
