package units.rider

import com.soywiz.korma.geom.IPoint
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.copy

sealed class RiderEvent {
    sealed class Move(private val point: IPoint) : RiderEvent() {
        object Up : Move(Point.Up)
        object Down : Move(Point.Down)
        object Left : Move(Point.Left)
        object Right : Move(Point.Right)

        fun point(): IPoint = point.copy()
    }
    class Shoot(val destination: IPoint) : RiderEvent()
}
