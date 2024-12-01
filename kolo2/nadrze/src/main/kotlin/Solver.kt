class Solver(private val entry: Entry)
{
    /* How many times a tank has been pumped */
    private val pumps = IntArray(entry.tankCount)

    fun solve(): List<Long>
    {
        val answers = mutableListOf<Long>()

        for (query in entry.queries)
        {
            when (query.type)
            {
                QueryType.LEVEL -> answers.add(getTankLevel(query.input))
                QueryType.PUMP -> pumpTank(query.input)
                QueryType.PUMP_POSSIBILITY -> answers.add(getPumpPossibility(query.input))
            }
        }
        return answers
    }

    private fun Int.parentIndex() = entry.parents[this]

    private fun getTankLevel(tank: Int): Long
    {
        var pumpCount = 0
        var current = tank
        while (current != -1)
        {
            pumpCount += pumps[current]
            current = current.parentIndex()
        }
        var level = entry.levels[tank]
        repeat(pumpCount) {
            level = level - (level + 1) / 2
        }
        return level
    }

    private fun pumpTank(tank: Int)
    {
        pumps[tank]++
    }

    private fun getPumpPossibility(tank: Int): Long
    {
        return -1
    }
}