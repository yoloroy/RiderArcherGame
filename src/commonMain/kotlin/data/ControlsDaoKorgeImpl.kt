package data

import Controls
import ControlsDao
import com.soywiz.korev.*
import com.soywiz.korio.concurrent.atomic.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.serialization.json.*
import kotlinx.coroutines.sync.*

class ControlsDaoKorgeImpl : ControlsDao {

    private var controls by KorAtomicRef(Controls(Key.W, Key.S, Key.A, Key.D))
    private var isChanged by KorAtomicBoolean(true)
    private val mutex = Mutex()

    override suspend fun saveControls(controls: Controls) {
        isChanged = true
        mutex.withLock {
            resourcesVfs["controls.json"].writeString(controls.toMap().toJson())
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun loadControls(): Controls {
        return if (isChanged) mutex.withLock {
            val controlsString = resourcesVfs["controls.json"].readString()
            val controlsMap = Json.parse(controlsString) as Map<String, String>
            Controls.fromMap(controlsMap).also {
                controls = it
                isChanged = false
            }
        } else {
            controls
        }
    }
}
