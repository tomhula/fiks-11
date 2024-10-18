class Race(
    val minStepTime: Int,
    val initialStepTime: Int,
    val maxStepTime: Int,
    val size: IntVector,
    val sectors: Set<Sector>,
    val startSector: Sector.Start,
    val endSector: Sector.End
)
{
    val stepTimeRange = minStepTime..maxStepTime

    operator fun contains(pos: IntVector) = pos.x in 0..<size.x
            && pos.y in 0..<size.y
            && pos.z in 0..<size.z
}