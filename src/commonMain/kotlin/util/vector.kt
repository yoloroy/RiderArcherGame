package util

import com.soywiz.korma.geom.*

fun IPoint.coerceLengthIn(range: ClosedRange<Double>): IPoint = when {
    length > range.endInclusive -> unit * range.endInclusive
    length < range.start -> unit * range.start
    else -> copy()
}
