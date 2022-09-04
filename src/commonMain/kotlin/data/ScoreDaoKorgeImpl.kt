package data

import ScoreDao
import com.soywiz.korio.concurrent.atomic.*
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.sync.*

class ScoreDaoKorgeImpl : ScoreDao {

    private var score by KorAtomicInt(0)
    private var isChanged by KorAtomicBoolean(true)
    private val mutex = Mutex()
    private val scoreFile get() = resourcesVfs["score.json"]

    override suspend fun saveScore(score: Int) {
        isChanged = true
        mutex.withLock {
            scoreFile.writeString(score.toString())
        }
    }

    override suspend fun loadScore(): Int {
        return if (isChanged) mutex.withLock {
            scoreFile.readString().toInt().also {
                score = it
                isChanged = false
            }
        } else {
            score
        }
    }
}
