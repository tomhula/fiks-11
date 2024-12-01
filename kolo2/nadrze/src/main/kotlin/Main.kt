fun main()
{
    val entries = parseInput()

    for (entry in entries)
    {
        val answers = Solver(entry).solve()
        answers.forEach(::println)
    }
}

private fun parseInput(): Array<Entry>
{
    val entriesCount = readln().toInt()

    return Array<Entry>(entriesCount) {
        val tanksCount = readln().toInt()
        val levels = LongArray(tanksCount) { readln().toLong() }
        var rootTank = mutableSetOf<Int>(*(0..<tanksCount).toList().toTypedArray())
        val parents = IntArray(tanksCount) { -1 }
        val children = Array<IntArray>(tanksCount) { index ->
            val line = readInts()
            val tankChildren = line.subList(1, line.size)
            for (child in tankChildren)
                parents[child] = index
            rootTank.removeAll(tankChildren)
            tankChildren.toIntArray()
        }
        val queriesCount = readln().toInt()
        val queries = Array<Query>(queriesCount) {
            val queryLine = readln().split(" ")
            val type = when (val queryString = queryLine[0])
            {
                "?" -> QueryType.LEVEL
                "!" -> QueryType.PUMP
                "#" -> QueryType.PUMP_POSSIBILITY
                else -> throw IllegalArgumentException("Invalid query type $queryString")
            }
            Query(type, queryLine[1].toInt())
        }

        Entry(levels, children, parents, queries, rootTank.single())
    }
}