package view.scenes

import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.text.*
import exitFunction

class MainMenuScene(private val score: Int? = null) : Scene() {
    override suspend fun SContainer.sceneMain() {
        uiVerticalStack {
            val scoreRepresentation = score?.let { ", score: $it" } ?: ""
            uiText("RiderArcherGame$scoreRepresentation") {
                textColor = Colors.BLACK
                textSize = 24.0
                textAlignment = TextAlignment.CENTER
            }
            uiSpacing(height = 24.0)
            uiButton(if (score == null) "Start game" else "Restart") {
                onClick { sceneContainer.changeTo({ GameScene() }) }
            }
            uiSpacing(height = 8.0)
            uiButton("Change controls") {
                onClick { sceneContainer.changeTo({ KeysSettingsScene() }) }
            }
            exitFunction?.let {
                uiSpacing(height = 8.0)
                uiButton("Exit") {
                    onClick { exitFunction() }
                }
            }
            centerOnStage()
        }
    }
}
