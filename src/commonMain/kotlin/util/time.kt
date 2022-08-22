package util

import com.soywiz.klock.*

operator fun Frequency.compareTo(other: Frequency): Int = hertz.compareTo(other.hertz)
