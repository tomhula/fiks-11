class Solver(val crew: Crew)
{
    private val leafMembersSorted = crew.membersSorted.filter { crew.leafMembersMask[it.index] }

    private var remainingPoints
        get() = crew.availablePoints
        set(value)
        {
            crew.availablePoints = value
        }

    fun solve(): Long
    {
        stabilise()

        val activeMembers = mutableSetOf<Member>()
        val remainingLeafMembers = leafMembersSorted.toMutableList()
        val surfaceMembers = mutableSetOf<Member>()
        var weakestPoints = leafMembersSorted.first().points

        for (member in leafMembersSorted)
        {
            if (member.points == weakestPoints)
            {
                activeMembers.add(member)
                remainingLeafMembers.remove(member)
                surfaceMembers.add(member)
            }
        }

        while (true)
        {
            var target: Member? = null
            var targetChild: Member? = null
            var smallestStep: Long? = null

            val firstRemainingLeafMember = remainingLeafMembers.firstOrNull()

            if (firstRemainingLeafMember != null)
            {
                target = firstRemainingLeafMember
                smallestStep = firstRemainingLeafMember.points - weakestPoints
            }

            for (surfaceMember in surfaceMembers)
            {
                val step = crew.getDistanceToBelowParent(surfaceMember)

                if (step != null && (smallestStep == null || step < smallestStep))
                {
                    target = crew.getParentOf(surfaceMember)
                    targetChild = surfaceMember
                    smallestStep = step
                }
            }

            if (target == null || smallestStep == null || remainingPoints < smallestStep * activeMembers.size || smallestStep * activeMembers.size < 0) // Integer overflow protection
            {
                val pointsToGive = remainingPoints / activeMembers.size
                return weakestPoints + pointsToGive
            }

            if (smallestStep != 0L)
                activeMembers.giveToEach(smallestStep)

            weakestPoints += smallestStep

            activeMembers.add(target)
            if (crew.isLeaf(target))
                remainingLeafMembers.remove(target)

            surfaceMembers.add(target)
            if (targetChild != null)
                surfaceMembers.remove(targetChild)
        }
    }

    /** Gives [amount] of points from [remainingPoints] to this member and returns its new total points. */
    private fun Member.givePoints(amount: Long): Long
    {
        points += amount
        remainingPoints -= amount
        return points
    }

    /** Gives each [Member] [amount] of points from [remainingPoints]. */
    private inline fun Iterable<Member>.giveToEach(amount: Long) = forEach { it.givePoints(amount) }

    /**
     * @throws OutOfPointsException when running out of points without reaching stability
     */
    private fun stabilise()
    {
        for (leafMember in leafMembersSorted)
        {
            var currentMember = leafMember

            while (crew.getParentOf(currentMember) != null)
            {
                val currentParent = crew.getParentOf(currentMember)!!
                val currentMemberPoints = currentMember.points
                val currentParentPoints = currentParent.points

                if (currentParentPoints <= currentMemberPoints)
                {
                    val neededPoints = currentMemberPoints - currentParentPoints + 1
                    if (remainingPoints < neededPoints)
                        throw OutOfPointsException()
                    currentParent.givePoints(neededPoints)
                }

                currentMember = currentParent
            }
        }
    }
}

class OutOfPointsException(message: String = "Ran out of points during stabilisation") : RuntimeException(message)