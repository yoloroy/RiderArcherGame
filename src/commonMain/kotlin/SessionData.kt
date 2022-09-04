import com.soywiz.korev.*

class SessionData(
    controlsDao: ControlsDao,
    scoreDao: ScoreDao
) : ControlsDao by controlsDao,
    ScoreDao by scoreDao

interface ScoreDao {

    suspend fun saveScore(score: Int)

    suspend fun loadScore(): Int
}

interface ControlsDao {

    suspend fun saveControls(controls: Controls)

    suspend fun loadControls(): Controls
}

data class Controls(
    val up: Key,
    val down: Key,
    val left: Key,
    val right: Key
) {

    companion object {
        fun fromMap(map: Map<String, String>) = Controls(
            Key.valueOf(map["up"]!!),
            Key.valueOf(map["down"]!!),
            Key.valueOf(map["left"]!!),
            Key.valueOf(map["right"]!!)
        )
    }

    fun toMap() = mapOf(
        "up" to up.name,
        "down" to down.name,
        "left" to left.name,
        "right" to right.name
    )
}
