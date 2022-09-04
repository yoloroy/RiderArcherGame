package view.scenes

import SessionData
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.text.*
import exitFunction

class MainMenuScene(private val sessionData: SessionData, private var score: Int? = null) : Scene() {

    override suspend fun SContainer.sceneInit() {
        score?.let { score ->
            sessionData.saveScore(score)
        } ?: run {
            score = sessionData.loadScore()
        }
    }

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
                onClick { sceneContainer.changeTo({ GameScene(sessionData) }) }
            }
            uiSpacing(height = 8.0)
            uiButton("Change controls") {
                onClick { sceneContainer.changeTo({ KeysSettingsScene(sessionData) }) }
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
