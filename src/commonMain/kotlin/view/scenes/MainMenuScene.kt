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

    private var maxScore: Int = score ?: 0

    override suspend fun SContainer.sceneInit() {
        score?.let { score ->
            val savedMaxScore = sessionData.loadScore()
            maxScore = if (score > savedMaxScore) {
                sessionData.saveScore(score)
                score
            } else {
                savedMaxScore
            }
        }
    }

    override suspend fun SContainer.sceneMain() {
        uiVerticalStack {
            val scoreRepresentation = score?.let { "\n  Score: $it" } ?: ""
            val maxScoreRepresentation = "\n  Max score: $maxScore"
            val title = "RiderArcherGame$maxScoreRepresentation$scoreRepresentation"
            uiText(title) {
                textColor = Colors.BLACK
                textSize = 24.0
                textAlignment = TextAlignment.CENTER
            }
            uiSpacing(height = 8.0 + 24 * (title.count { it == '\n' } + 1))
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
