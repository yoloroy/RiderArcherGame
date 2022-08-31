import com.soywiz.korge.gradle.*

plugins {
	alias(libs.plugins.korge)
}

korge {
	id = "com.yoloroy.rider_archer_game"
    version = "0.1"
    exeBaseName = "rag"
    name = "RiderArcherGame"
    description = "Game about RiderArcher"

	targetJvm()
	targetJs()
	targetDesktop()
}
