fun main()
{
    val entries = parseInput()

    for (entry in entries)
    {
        val crew = entry.crew
        val availablePoints = entry.availablePoints

        try
        {
            val remainingPoints = stabilise(crew, availablePoints)
            val memberWithLeastPoints = distributePoints(crew, remainingPoints)
            println(memberWithLeastPoints.points)
        }
        catch (e: OutOfPointsException)
        {
            println("ajajaj")
        }
    }
}

/**
 * @return the [Member] with the least points.
 */
fun distributePoints(crew: Crew, availablePoints: Long): Member
{
    if (crew.size == 1)
    {
        crew.leader.points += availablePoints
        return crew.leader
    }

    var remainingPoints = availablePoints
    val membersLeastToMost = crew.getLeafMembers().sortedBy{ it.points }.toMutableList()

    while (remainingPoints > 0)
    {
        val first = membersLeastToMost.first()
        val second = membersLeastToMost.getOrNull(1)

        val pointsNeededForStabilisation = branchStabilisationPointsNeeded(first, crew, 1)

        if (pointsNeededForStabilisation + 1 <= remainingPoints)
        {
            first.points++
            remainingPoints--
            remainingPoints = stabiliseBranch(first, crew, remainingPoints)
        }
        else
            break

        if (second == null)
            break

        if (first.points > second.points)
        {
            for (i in 1 until membersLeastToMost.size)
            {
                val member = membersLeastToMost[i]

                if (member.points >= first.points)
                {
                    membersLeastToMost.add(i, first)
                    membersLeastToMost.removeFirst()
                    break
                }
                /* No number is larger, so go to the end */
                else if (i == membersLeastToMost.size - 1)
                {
                    membersLeastToMost.add(first)
                    membersLeastToMost.removeFirst()
                    break
                }
            }
        }
    }

    return membersLeastToMost.first()
}

fun parseInput(): List<Entry>
{
    val numOfEntries = readln().toInt()
    val entries = mutableListOf<Entry>()

    repeat(numOfEntries) {
        val firstLine = readLongs()
        val numOfMembers = firstLine[0].toInt()
        val availablePoints = firstLine[1]
        val pointsLine = readLongs()
        val superiorsLine = readInts()

        /* Superior id to set of it's inferiors */
        val inferiorIds = mutableMapOf<Int, MutableSet<Int>>()
        /* Inferior id to its superior's id */
        val superiorIds = mutableMapOf<Int, Int>()

        for (i in 0..< numOfMembers)
        {
            val id = i + 1
            val superiorId = superiorsLine[i]

            superiorIds[id] = superiorId
            if (superiorId != -1)
                inferiorIds.getOrPut(superiorId, { mutableSetOf() }).add(id)
        }

        for (member in superiorIds.keys)
            if (!inferiorIds.containsKey(member))
                inferiorIds[member] = mutableSetOf()

        val crew = Crew(inferiorIds, superiorIds, pointsLine)
        val entry = Entry(availablePoints, crew)
        entries.add(entry)
    }

    return entries
}

/**
 * Calls [stabiliseBranch] on leaf members.
 * @param availablePoints amount of points available
 * @throws OutOfPointsException when running out of points without reaching stability
 */
private fun stabilise(crew: Crew, availablePoints: Long): Long
{
    val leafMembers = crew.getLeafMembers()
    var remainingPoints = availablePoints

    for (leafMember in leafMembers)
        /* branchStabilisationPointsNeeded check not needed, because the crew ends once it is impossible to stabilise */
        remainingPoints = stabiliseBranch(leafMember, crew, remainingPoints)

    return remainingPoints
}

/**
 * Gives points from [availablePoints] to superior of [member], so superior's points > [member]'s points.
 * If any points were given, recursively calls this function with superior, until leader. (no more superior)
 * [branchStabilisationPointsNeeded] should be called before this function to check if there are enough points to stabilise the branch.
 * @return remaining points after giving out
 * @throws OutOfPointsException when running out of points without reaching stability
 */
private fun stabiliseBranch(member: Member, crew: Crew, availablePoints: Long): Long
{
    val superior = crew.getSuperiorTo(member)
    var remainingPoints = availablePoints

    if (superior == null)
        return remainingPoints

    if (superior.points <= member.points)
    {
        val neededPoints = member.points - superior.points + 1
        if (remainingPoints < neededPoints)
            throw OutOfPointsException()
        superior.points += neededPoints
        remainingPoints -= neededPoints

        if (crew.getSuperiorTo(superior) != null)
            return stabiliseBranch(superior, crew, remainingPoints)
        else
            return remainingPoints
    }
    else
        return remainingPoints
}

private fun branchStabilisationPointsNeeded(member: Member, crew: Crew, memberPointsOffset: Long = 0): Long
{
    var totalPointsNeeded = 0L

    fun branchStabilisationPointsNeededRec(member: Member, memberPointsOffset: Long)
    {
        val superior = crew.getSuperiorTo(member) ?: return

        val superiorPoints = superior.points
        val memberPoints = member.points + memberPointsOffset

        if (superiorPoints <= memberPoints)
        {
            val neededPoints = memberPoints - superiorPoints + 1
            totalPointsNeeded += neededPoints
            branchStabilisationPointsNeededRec(superior, neededPoints)
        }
        else
            return
    }

    branchStabilisationPointsNeededRec(member, memberPointsOffset)

    return totalPointsNeeded
}

private fun readInts(): List<Int> = readln().split(" ").map(String::toInt)

private fun readLongs(): List<Long> = readln().split(" ").map(String::toLong)

data class Entry(
    val availablePoints: Long,
    val crew: Crew
)

class Crew(crew: Map<Int, Set<Int>>, members: Map<Int, Int>, private val points: List<Long>)
{
    private val membersById: Map<Int, Member> = members.keys.map { createMember(it) }.associateBy { it.id }
    private val crew: Map<Member, Set<Member>> = crew.mapKeys { getMemberById(it.key)!! }.mapValues { it.value.map { id -> getMemberById(id)!! }.toSet() }
    private val membersSuperiors: Map<Member, Member?> = members.mapKeys { getMemberById(it.key)!! }.mapValues { if (it.value == -1) null else getMemberById(it.value) }
    private val leafMembers = membersById.values.filter { it !in membersSuperiors.values }
    val size = membersById.size
    val leader = membersSuperiors.entries.find { it.value == null }!!.key

    private fun getMemberById(id: Int) = membersById[id]

    fun getSuperiorTo(inferior: Member) = membersSuperiors[inferior]
    fun getInferiorsTo(superior: Member) = crew[superior]!!
    fun isLeader(member: Member) = member === leader
    fun getAllMembers() = membersById.values
    fun getLeafMembers() = leafMembers

    fun copy() = Crew(
        this.crew.mapKeys { it.key.id }.mapValues { it.value.map { member -> member.id }.toSet() },
        membersSuperiors.mapKeys { it.key.id }.mapValues { it.value?.id ?: -1 },
        membersById.entries.sortedBy { it.key }.map { it.value.points }
    )

    private fun createMember(id: Int) = Member(id, points[id - 1])

    override fun toString() = crew.toString()
}

data class Member(
    val id: Int,
    var points: Long
)
{
    override fun toString() = "$id>$points"

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false
        other as Member
        return id == other.id
    }

    override fun hashCode() = id.hashCode()
}

private class OutOfPointsException : RuntimeException("Ran out of points during stabilisation")