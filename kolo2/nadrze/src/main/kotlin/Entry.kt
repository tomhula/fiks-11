data class Entry(
    val levels: LongArray,
    val children: Array<IntArray>,
    val parents: IntArray,
    val queries: Array<Query>,
    val rootTank: Int
)
{
    val tankCount = levels.size
}
