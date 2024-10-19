sealed class Sector(val pos: IntVector)
{
    class Speed(pos: IntVector, val stepTimeModifier: Int) : Sector(pos)
    class Start(pos: IntVector) : Sector(pos)
    class End(pos: IntVector) : Sector(pos)
    class NoGo(pos: IntVector) : Sector(pos)

    fun copy(pos: IntVector) = when (this)
    {
        is Speed -> Speed(pos, stepTimeModifier)
        is Start -> Start(pos)
        is End -> End(pos)
        is NoGo -> NoGo(pos)
    }

    override fun toString(): String = when (this)
    {
        is Speed -> "$stepTimeModifier"
        is Start -> "B"
        is End -> "E"
        is NoGo -> "F"
    }

    override fun hashCode() = pos.hashCode()

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other !is Sector) return false

        /* Note that time of Sector.Speed is not compared, since there can't be two sectors on same place anyway */
        return pos == other.pos
    }
}