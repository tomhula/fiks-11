data class IntVector(val x: Int, val y: Int, val z: Int)
{
    val above: IntVector
        get() = IntVector(x, y + 1, z)
    val below: IntVector
        get() = IntVector(x, y - 1, z)
    val front: IntVector
        get() = IntVector(x, y, z + 1)
    val back: IntVector
        get() = IntVector(x, y, z - 1)
    val left: IntVector
        get() = IntVector(x - 1, y, z)
    val right: IntVector
        get() = IntVector(x + 1, y, z)

    val neighbours: List<IntVector>
        get() = listOf(above, below, front, back, left, right)

    operator fun plus(other: IntVector) = IntVector(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: IntVector) = IntVector(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Int) = IntVector(x * scalar, y * scalar, z * scalar)

    override fun toString() = "[$x, $y, $z]"

    override fun hashCode(): Int
    {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other !is IntVector) return false

        return x == other.x && y == other.y && z == other.z
    }
}