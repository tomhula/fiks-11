class Race(
    val minStepTime: Int,
    val initialStepTime: Int,
    val maxStepTime: Int,
    val size: IntVec,
    val sectors: Set<Sector>,
    val startSector: Sector.Start,
    val endSector: Sector.End
)
{
    val stepTimeRange = minStepTime..maxStepTime
}