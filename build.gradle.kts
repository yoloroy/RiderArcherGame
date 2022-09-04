import com.soywiz.korge.gradle.*

plugins {
	alias(libs.plugins.korge)
}

korge {
	id = "com.yoloroy.rider_archer_game"
    icon = project.file("src/commonMain/resources/riderArcher.png")
    versionCode = 2
    version = "0.2"
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

    supportBox2d()
}
