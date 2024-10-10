fun main()
{
    val crews = parseInput()

    for (crew in crews)
    {
        val solver = Solver(crew)

        try
        {
//            println("Solving $i (${(i*3-1)})")
            val weakestMemberPoints = solver.solve()
            println(weakestMemberPoints)
        }
        catch (e: OutOfPointsException)
        {
            println("ajajaj")
        }
    }
}

fun parseInput(): List<Crew>
{
    val numOfEntries = readln().toInt()
    val crews = mutableListOf<Crew>()

    repeat(numOfEntries) {
        val firstLine = readLongs()
        val numOfMembers = firstLine[0].toInt()
        val availablePoints = firstLine[1]
        val pointsLine = readLongs()
        val parentsLine = readInts()

        val crew = buildCrew(numOfMembers, availablePoints) {
            for (i in 0..<numOfMembers)
                addMember(i, parentsLine[i] - 1, pointsLine[i])
        }

        crews.add(crew)
    }

    return crews
}