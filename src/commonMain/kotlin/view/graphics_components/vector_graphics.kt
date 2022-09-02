package view.graphics_components

import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.vector.*

fun Container.arrow(color: RGBA = Colors.BLACK, block: View.() -> Unit = {}) = graphics {
    moveTo(100, 10)
    lineTo(80, 0)
    lineTo(80, 9)
    lineTo(0, 9)
    lineTo(0, 11)
    lineTo(80, 11)
    lineTo(80, 20)
    lineTo(100, 10)
    close()
    fill(color)
}.apply(block)
