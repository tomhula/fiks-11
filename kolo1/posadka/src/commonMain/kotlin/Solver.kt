class Solver(val crew: Crew)
{
    private val membersSorted = crew.members.sortedBy { it.points }
    private val leafMembersSorted = membersSorted.filter { it.children.isEmpty() }

    private var remainingPoints
        get() = crew.remainingPoints
        set(value)
        {
            crew.remainingPoints = value
        }

    fun solve(): Long
    {
        stabilise(crew)
//        log{ "STABILISED CREW:" }
//        log{ crew }

        val activeMembers = mutableSetOf<Member>()
        val remainingLeafMembers = leafMembersSorted.toMutableSet()
        val surfaceMembers = mutableSetOf<Member>()
        var weakestPoints = leafMembersSorted.first().points

        for (member in leafMembersSorted)
        {
            if (member.points == weakestPoints)
            {
                activeMembers.add(member)
                if (member.isLeaf)
                    remainingLeafMembers.remove(member)
                surfaceMembers.add(member)
            }
        }

//        log { "leaf members: ${leafMembersSorted.joinToString { it.signature }}" }

        while (true)
        {
//            log { "active members: ${activeMembers.joinToString { it.signature }}" }
//            log { "surface members: ${surfaceMembers.joinToString { it.signature }}" }
//            log { "remaining leaf members: ${remainingLeafMembers.joinToString { it.signature }}" }
//            log { "weakest points: $weakestPoints" }

            var target: Member? = null
            var smallestStep: Long? = null

            for (leafMember in remainingLeafMembers)
            {
                val step = leafMember.points - weakestPoints

                if (smallestStep == null || step < smallestStep)
                {
                    target = leafMember
                    smallestStep = step
                }
            }

            for (surfaceMember in surfaceMembers)
            {
                val step = surfaceMember.distanceToBelowParent

                if (step != null && (smallestStep == null || step < smallestStep))
                {
                    target = surfaceMember.parent
                    smallestStep = step
                }
            }

//            log { "Target is: $target with a step of $smallestStep" }

            if (target == null || smallestStep == null || remainingPoints < smallestStep * activeMembers.size || smallestStep * activeMembers.size < 0) // Integer overflow protection
            {
                val pointsToGive = remainingPoints / activeMembers.size
                return weakestPoints + pointsToGive
            }

            if (smallestStep != 0L)
                activeMembers.giveToEach(smallestStep)

            weakestPoints += smallestStep

            activeMembers.add(target)
            if (target.isLeaf)
                remainingLeafMembers.remove(target)

            surfaceMembers.add(target)
            surfaceMembers.removeAll(target.children)
        }
    }

    /** Gives [amount] of points from [remainingPoints] to this member and returns its new total points. */
    private fun Member.givePoints(amount: Long): Long
    {
//        log { "Giving $amount points to $this [[${remainingPoints - amount}]]" }
        points += amount
        remainingPoints -= amount
        return points
    }

    /** Gives each [Member] [amount] of points from [remainingPoints]. */
    private inline fun Iterable<Member>.giveToEach(amount: Long) = forEach { it.givePoints(amount) }

    /**
     * @throws OutOfPointsException when running out of points without reaching stability
     */
    private fun stabilise(crew: Crew)
    {
        for (leafMember in crew.leafMembers)
        {
            var currentMember = leafMember

            while (currentMember.parent != null)
            {
                val currentParent = currentMember.parent!!
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