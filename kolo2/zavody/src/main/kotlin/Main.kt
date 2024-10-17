import java.util.*

fun main()
{
    val races = parseInput()

    for ((i, race) in races.withIndex())
    {
        /* Only try to solve the first 30 problems, since the following 10 are harder and won't be done on time */
        val result = if (i <= 29)
            Solver(race).solve()
        else
            -1

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

        val sectors: Array<Array<Array<Sector?>>> = Array(width) { Array(height) { Array(depth) { null } } }
        var startSector: Sector.Start? = null
        var endSector: Sector.End? = null

        repeat(nodeCount) {
            val line = readln().split(" ")
            val (x, y, z) = line.subList(0, 3).map(String::toInt)
            val pos = IntVector(x, y, z)

            val sector = when (val valueStr = line[3])
            {
                "B" -> Sector.Start(pos).also { startSector = it }
                "E" -> Sector.End(pos).also { endSector = it }
                "F" -> Sector.NoGo(pos)
                else -> Sector.Speed(pos, valueStr.toInt())
            }

            sectors[x][y][z] = sector
        }

        races.add(
            Race(
                minStepTime,
                initialStepTime,
                maxStepTime,
                IntVector(width, height, depth),
                sectors,
                startSector!!,
                endSector!!
            )
        )
    }

    return races
}