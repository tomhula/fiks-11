import kotlin.math.atan2
import kotlin.math.sqrt

data class Vec(val x: Float, val y: Float)
{
    constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat())

    operator fun plus(other: Vec) = Vec(x + other.x, y + other.y)

    operator fun minus(other: Vec) = Vec(x - other.x, y - other.y)

    operator fun times(scalar: Float) = Vec(x * scalar, y * scalar)

    operator fun div(scalar: Float): Vec
    {
        require(scalar != 0f) { "Division by zero is not allowed." }
        return Vec(x / scalar, y / scalar)
    }

    infix fun dot(other: Vec) = x * other.x + y * other.y

    fun angle() = atan2(y.toDouble(), x.toDouble())

    fun magnitude() = sqrt(x * x + y * y)

    fun normalize(): Vec
    {
        val mag = magnitude()
        require(mag != 0f) { "Cannot normalize a zero vector." }
        return this / mag
    }

    fun distanceToSquared(other: Vec) = (other - this).let { it.x * it.x + it.y * it.y }

    fun distanceTo(other: Vec) = sqrt(distanceToSquared(other))

    override fun toString() = "[$x, $y]"

    companion object
    {
        val ZERO = Vec(0, 0)
        val ONE = Vec(1, 1)
    }
}
