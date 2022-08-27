package util

import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import core.*

val View.sizePoint get() = Point(width, height)

fun View.asPosProvider(anchor: Anchor = Anchor.TOP_LEFT) = PosProvider { pos.copy() + sizePoint * anchor.toPoint() }
