import org.apfloat.Apfloat

data class Vec(val x: Apfloat, val y: Apfloat)
{
    operator fun plus(other: Vec) = Vec(x + other.x, y + other.y)

    operator fun minus(other: Vec) = Vec(x - other.x, y - other.y)

    operator fun times(scalar: Apfloat) = Vec(x * scalar, y * scalar)

    operator fun div(scalar: Apfloat): Vec
    {
        require(!scalar.isZero) { "Division by zero is not allowed." }
        return Vec(x / scalar, y / scalar)
    }

    infix fun dot(other: Vec) = x * other.x + y * other.y

    fun angle() = atan2(y, x)

    fun magnitude() = sqrt(x * x + y * y)

    fun normalize(): Vec
    {
        val mag = magnitude()
        require(!mag.isZero) { "Cannot normalize a zero vector." }
        return this / mag
    }

    fun distanceToSquared(other: Vec) = (other - this).let { it.x * it.x + it.y * it.y }

    fun distanceTo(other: Vec) = sqrt(distanceToSquared(other))

    override fun toString() = "[$x, $y]"

    companion object
    {
        val ZERO = Vec(Apfloat.ZERO, Apfloat.ZERO)
    }
}
