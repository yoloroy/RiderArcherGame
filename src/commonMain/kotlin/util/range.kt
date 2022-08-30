package util

import com.soywiz.korma.random.*
import kotlin.random.*

fun ClosedRange<Double>.random() = Random[start, endInclusive]
