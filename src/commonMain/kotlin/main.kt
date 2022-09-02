import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korim.color.*
import view.scenes.*

suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors.WHEAT) {
    val sceneContainer = sceneContainer()

    sceneContainer.changeTo({ MainMenuScene() })
}
