import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korim.color.*
import data.*
import view.scenes.*

suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors.BLACK) {
    val sceneContainer = sceneContainer()
    val sessionData = SessionData(ControlsDaoKorgeImpl(), ScoreDaoKorgeImpl())
    sceneContainer.changeTo({ MainMenuScene(sessionData) })
}
