fun main()
{
    val crews = parseInput()

    for (crew in crews)
    {
        val solver = Solver(crew)

        try
        {
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

        val members = Array<Member?>(numOfMembers) { null }
        val membersSorted = Array<Member?>(numOfMembers) { null }
        val leafMembersMask = BooleanArray(numOfMembers) { true }

        repeat(numOfMembers) { i ->
            val parentIndex = if (parentsLine[i] == -1) null else parentsLine[i] - 1
            val member = Member(i, parentIndex, pointsLine[i])
            members[i] = member
            if (parentIndex != null)
                leafMembersMask[parentIndex] = false

            // Insert member into the correct position in membersSorted to keep it sorted
            var j = i
            while (j > 0 && membersSorted[j - 1]!!.points > member.points) {
                membersSorted[j] = membersSorted[j - 1]
                j--
            }
            membersSorted[j] = member
        }

        crews.add(Crew(members.requireNoNulls(), membersSorted.requireNoNulls(), leafMembersMask, availablePoints))
    }

    return crews
}