import java.util.*

fun main()
{
    val races = parseInput()

    for (race in races)
    {
        val result = Solver(race).solve()
        println(result)
    }
}

fun parseInput(): List<Race>
{
    val races: MutableList<Race> = LinkedList()
    val numOfRaces = readln().toInt()

    repeat(numOfRaces) {
        val (minStepTime, maxStepTime, initialStepTime) = readInts()
        val (width, height, depth, nodeCount) = readInts()

        val sectors = mutableSetOf<Sector>()
        var startSector: Sector.Start? = null
        var endSector: Sector.End? = null

        repeat(nodeCount) {
            val line = readln().split(" ")
            val (x, y, z) = line.subList(0, 3).map(String::toInt)
            val pos = IntVec(x, y, z)

            val sector = when (val valueStr = line[3])
            {
                "B" -> Sector.Start(pos).also { startSector = it }
                "E" -> Sector.End(pos).also { endSector = it }
                "F" -> Sector.NoGo(pos)
                else -> Sector.Speed(pos, valueStr.toInt())
            }

            sectors.add(sector)
        }

        races.add(
            Race(
                minStepTime,
                initialStepTime,
                maxStepTime,
                IntVec(width, height, depth),
                sectors,
                startSector!!,
                endSector!!
            )
        )
    }

    return races
}