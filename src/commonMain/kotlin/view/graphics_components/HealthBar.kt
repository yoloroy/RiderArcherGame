package view.graphics_components

import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import view.graphics_components.HealthBarViewHolder.CreationCallback

class HealthBarViewHolder(
    private val widthWithoutStroke: Double,
    private val heightWithoutStroke: Double,
    private var backgroundColor: RGBA,
    private var fillColor: RGBA,
    private var emptyColor: RGBA,
    private val backgroundStrokeWidth: Double,
    private var healthPercent: Double = 1.0,
    private val creationCallback: CreationCallback = CreationCallback.Unit
) {
    private lateinit var mainContainer: Container
    private lateinit var healthBackground: SolidRect
    private lateinit var healthFill: SolidRect
    private lateinit var healthEmpty: SolidRect

    fun attachTo(container: Container) {
        mainContainer = container.fixedSizeContainer(
            widthWithoutStroke + backgroundStrokeWidth * 2,
            heightWithoutStroke + backgroundStrokeWidth * 2
        ) {
            healthBackground = solidRect(width, height, backgroundColor)
            healthFill = solidRect(widthWithoutStroke * healthPercent, heightWithoutStroke, fillColor) {
                position(Point(backgroundStrokeWidth))
            }
            healthEmpty = solidRect(widthWithoutStroke * (1 - healthPercent), heightWithoutStroke, emptyColor) {
                anchor(1.0, 0.0)
                position(Point(backgroundStrokeWidth) + Point(widthWithoutStroke, 0.0))
            }
        }
        creationCallback.callback(mainContainer, healthBackground, healthFill, healthEmpty)
    }

    fun update(
        healthPercent: Double = this.healthPercent,
        backgroundColor: RGBA = this.backgroundColor,
        fillColor: RGBA = this.fillColor,
        emptyColor: RGBA = this.emptyColor,
    ) {
        this.healthPercent = healthPercent
        this.backgroundColor = backgroundColor
        this.fillColor = fillColor
        this.emptyColor = emptyColor
        healthFill.scaledWidth = widthWithoutStroke * healthPercent
        healthEmpty.scaledWidth = widthWithoutStroke * (1 - healthPercent)
        healthBackground.color = backgroundColor
        healthFill.color = fillColor
        healthEmpty.color = emptyColor
    }

    fun interface CreationCallback {
        fun callback(container: Container, background: SolidRect, fill: SolidRect, empty: SolidRect)

        object Unit : CreationCallback {
            override fun callback(container: Container, background: SolidRect, fill: SolidRect, empty: SolidRect) {}
        }
    }
}

fun Container.healthBar(
    width: Double,
    height: Double,
    backgroundColor: RGBA,
    fillColor: RGBA,
    emptyColor: RGBA,
    backgroundStrokeWidth: Double,
    healthPercent: Double = 1.0,
    creationCallback: CreationCallback = CreationCallback.Unit
) = HealthBarViewHolder(
    width,
    height,
    backgroundColor,
    fillColor,
    emptyColor,
    backgroundStrokeWidth,
    healthPercent,
    creationCallback
).also { it.attachTo(this) }
