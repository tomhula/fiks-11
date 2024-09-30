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
    val membersLeastToMost = crew.leafMembers.sortedBy{ it.points }.toMutableList()

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
        val parentsLine = readInts()

        /* Parent id to set of it's children */
        val parentToChildrenIds = mutableMapOf<Int, MutableSet<Int>>()
        /* Child id to its parent's id */
        val membersToParentIds = mutableMapOf<Int, Int>()

        for (i in 0..< numOfMembers)
        {
            val id = i + 1
            val parentId = parentsLine[i]

            membersToParentIds[id] = parentId
            if (parentId != -1)
                parentToChildrenIds.getOrPut(parentId, { mutableSetOf() }).add(id)
        }

        for (member in membersToParentIds.keys)
            if (!parentToChildrenIds.containsKey(member))
                parentToChildrenIds[member] = mutableSetOf()

        val crew = Crew(parentToChildrenIds, membersToParentIds, pointsLine)
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
    var remainingPoints = availablePoints

    for (leafMember in crew.leafMembers)
        /* branchStabilisationPointsNeeded check not needed, because the crew ends once it is impossible to stabilise */
        remainingPoints = stabiliseBranch(leafMember, crew, remainingPoints)

    return remainingPoints
}

/**
 * Gives points from [availablePoints] to a parent of [member], so parent's points > [member]'s points.
 * If any points were given, recursively calls this function with parent, until leader. (no more parent)
 * [branchStabilisationPointsNeeded] should be called before this function to check if there are enough points to stabilise the branch.
 * @return remaining points after giving out
 * @throws OutOfPointsException when running out of points without reaching stability
 */
private fun stabiliseBranch(member: Member, crew: Crew, availablePoints: Long): Long
{
    val parent = crew.getParentOf(member)
    var remainingPoints = availablePoints

    if (parent == null)
        return remainingPoints

    if (parent.points <= member.points)
    {
        val neededPoints = member.points - parent.points + 1
        if (remainingPoints < neededPoints)
            throw OutOfPointsException()
        parent.points += neededPoints
        remainingPoints -= neededPoints

        if (crew.getParentOf(parent) != null)
            return stabiliseBranch(parent, crew, remainingPoints)
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
        val parent = crew.getParentOf(member) ?: return

        val parentPoints = parent.points
        val memberPoints = member.points + memberPointsOffset

        if (parentPoints <= memberPoints)
        {
            val neededPoints = memberPoints - parentPoints + 1
            totalPointsNeeded += neededPoints
            branchStabilisationPointsNeededRec(parent, neededPoints)
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
    private val memberToParent: Map<Member, Member?> = members.mapKeys { getMemberById(it.key)!! }.mapValues { if (it.value == -1) null else getMemberById(it.value) }
    val leafMembers = membersById.values.filter { it !in memberToParent.values }
    val allMembers = membersById.values
    val size = membersById.size
    val leader = memberToParent.entries.find { it.value == null }!!.key

    private fun getMemberById(id: Int) = membersById[id]

    fun getParentOf(child: Member) = memberToParent[child]
    fun getChildrenOf(parent: Member) = crew[parent]!!
    fun isLeader(member: Member) = member === leader

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