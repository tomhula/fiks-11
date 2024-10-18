import java.util.*

fun main()
{
    val races = parseInput()

     for ((i, race) in races.withIndex())
     {
        /* Only try to solve the first 30 problems, since the following 10 are harder and won't be done on time */
        //val result = if (i <= 29)
         val solve = Solver(race).solve()
         //else
        //    -1

        println("$i: $solve")
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
            val pos = IntVector(x, y, z)

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
                IntVector(width, height, depth),
                sectors,
                startSector!!,
                endSector!!
            )
        )
    }

    return races
}

fun printSpace(space: Array<Array<Array<Sector?>>>)
{
    for (z in space[0][0].indices)
    {
        println("z = $z")
        for (y in space[0].indices.reversed())
        {
            for (x in space.indices)
            {
                val str = when (val sector = space[x][y][z])
                {
                    is Sector.Start -> "S"
                    is Sector.End -> "E"
                    is Sector.NoGo -> "X"
                    is Sector.Speed -> sector.stepTimeModifier.toString()
                    else -> "."
                }
                print(str.padEnd(2))
            }
            println()
        }
        println()
    }
}