data class IntVector(val x: Int, val y: Int, val z: Int)
{
    val above: IntVector by lazy { IntVector(x, y + 1, z) }
    val below: IntVector by lazy { IntVector(x, y - 1, z) }
    val front: IntVector by lazy { IntVector(x, y, z + 1) }
    val back: IntVector by lazy { IntVector(x, y, z - 1) }
    val left: IntVector by lazy { IntVector(x - 1, y, z) }
    val right: IntVector by lazy { IntVector(x + 1, y, z) }

    /* All non-diagonal neighbour vectors */
    val neighbours: List<IntVector> by lazy { listOf(above, below, front, back, left, right) }

    /* Used for debugging */
    fun getCloseRelativeDirection(other: IntVector) = when (other)
    {
        above -> "UP"
        below -> "DOWN"
        front -> "FORWARD"
        back -> "BACK"
        left -> "LEFT"
        right -> "RIGHT"
        else -> "???"
    }

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