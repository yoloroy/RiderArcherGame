import com.soywiz.korge.gradle.*

plugins {
	alias(libs.plugins.korge)
}

korge {
	id = "com.yoloroy.rider_archer_game"
    versionCode = 1
    version = "0.1"
    exeBaseName = "rag"
    name = "RiderArcherGame"
    title = name
    description = "Game about RiderArcher"
    authorName = "yojick"
    authorHref = "https://yojick.itch.io"
    copyright = copyright.replace("unknown", "$authorName https://github.com/yoloroy/RiderArcherGame/blob/master/LICENSE")

	targetJvm()
	targetJs()
	targetDesktop()
}
