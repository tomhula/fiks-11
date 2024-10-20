data class IntVec(val x: Int, val y: Int, val z: Int)
{
    val above: IntVec by lazy { IntVec(x, y + 1, z) }
    val below: IntVec by lazy { IntVec(x, y - 1, z) }
    val front: IntVec by lazy { IntVec(x, y, z + 1) }
    val back: IntVec by lazy { IntVec(x, y, z - 1) }
    val left: IntVec by lazy { IntVec(x - 1, y, z) }
    val right: IntVec by lazy { IntVec(x + 1, y, z) }

    /* All non-diagonal neighbour vectors */
    val neighbours: List<IntVec> by lazy { listOf(above, below, front, back, left, right) }

    operator fun plus(other: IntVec) = IntVec(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: IntVec) = IntVec(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Int) = IntVec(x * scalar, y * scalar, z * scalar)

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
        if (other !is IntVec) return false

        return x == other.x && y == other.y && z == other.z
    }
}

/** Returns whether a 3D array contains [pos]. */
operator fun <T> Array<Array<Array<T>>>.contains(pos: IntVec) = pos.x in indices && pos.y in this[0].indices && pos.z in this[0][0].indices