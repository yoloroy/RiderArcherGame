import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korim.color.*
import scenes.*

suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors.WHEAT) {
    val sceneContainer = sceneContainer()

    sceneContainer.changeTo({ MainMenuScene() })
}
