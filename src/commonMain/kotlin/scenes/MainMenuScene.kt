package scenes

import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.text.*
import kotlin.system.*

class MainMenuScene : Scene() {
    override suspend fun SContainer.sceneMain() {
        uiVerticalStack {
            uiText("RiderArcherGame") {
                textColor = Colors.BLACK
                textSize = 24.0
                textAlignment = TextAlignment.CENTER
            }
            uiSpacing(height = 24.0)
            uiButton("Start game") {
                onClick { sceneContainer.changeTo({ GameScene() }) }
            }
            uiSpacing(height = 8.0)
            uiButton("Exit") {
                onClick { exitProcess(0) }
            }
            centerOnStage()
        }
    }
}