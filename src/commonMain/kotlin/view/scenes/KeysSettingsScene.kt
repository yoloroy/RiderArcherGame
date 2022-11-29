package view.scenes

import Controls
import SessionData
import com.soywiz.korev.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.text.*
import com.soywiz.korio.async.*

class KeysSettingsScene(
    private val sessionData: SessionData,
    returnToMenu: ReturnToMenu
) : Scene(), ReturnToMenu by returnToMenu {

    private lateinit var controls: Controls

    private val keysButtonData by lazy {
        listOf(
            KeyButtonData("up", controls.up) { updateControls(controls.copy(up = it)) },
            KeyButtonData("down", controls.down) { updateControls(controls.copy(down = it)) },
            KeyButtonData("left", controls.left) { updateControls(controls.copy(left = it)) },
            KeyButtonData("right", controls.right) { updateControls(controls.copy(right = it)) }
        )
    }
    private var buttons = listOf<UIButton>()

    override suspend fun SContainer.sceneInit() {
        controls = sessionData.loadControls()
    }

    override suspend fun SContainer.sceneMain() {
        solidRect(width, height, color = Colors.SLATEGRAY + RGBA(0x33, 0x33, 0x33))

        var isKeyChangingStarted = false // TODO refactor
        var currentKeyIndex: Int = -1

        uiVerticalStack {
            uiText("Controls") {
                textColor = Colors.BLACK
                textSize = 24.0
                textAlignment = TextAlignment.CENTER
            }
            uiSpacing(height = 24.0)
            for ((index, keyButtonData) in keysButtonData.withIndex()) {
                val (name, currentKey) = keyButtonData
                buttons += uiButton("$name: ${currentKey.name}") {
                    onClick {
                        isKeyChangingStarted = true
                        currentKeyIndex = index
                    }
                }
                uiSpacing(height = 8.0)
            }
            uiSpacing(height = 8.0)
            uiButton("Return") {
                onClick { sceneContainer.launchReturnToMenu() }
            }
            centerOnStage()
        }

        keys {
            down { key ->
                if (!isKeyChangingStarted) return@down
                isKeyChangingStarted = false
                val keyButtonData = keysButtonData[currentKeyIndex]
                buttons[currentKeyIndex].text = "${keyButtonData.name}: ${key.key.name}" // TODO string template
                keyButtonData.changeKey(key.key)
            }
        }
    }

    private fun updateControls(newControls: Controls) {
        controls = newControls
        launch {
            sessionData.saveControls(newControls)
        }
    }

    private data class KeyButtonData(
        val name: String,
        val currentKey: Key,
        val changeKey: (Key) -> Unit
    )
}
