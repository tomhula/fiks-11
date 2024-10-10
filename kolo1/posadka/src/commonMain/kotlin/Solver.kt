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
        log("leader: ${crew.leader}")
        log("STABILISED CREW:")
        log(crew)

        val activeMembers = mutableSetOf<Member>()
        val remainingLeafMembers = leafMembersSorted.toMutableSet()
        var surfaceMembers = mutableSetOf<Member>()
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

        log("leaf members: ${leafMembersSorted.joinToString { it.signature }}")

        while (true)
        {
            log("active members: ${activeMembers.joinToString { it.signature }}")
            log("surface members: ${surfaceMembers.joinToString { it.signature }}")
            log("remaining leaf members: ${remainingLeafMembers.joinToString { it.signature }}")
            log("weakest points: $weakestPoints")

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

            log("Target is: $target with a step of $smallestStep")

            if (target == null || smallestStep == null)
            {
                val pointsToGive = remainingPoints / activeMembers.size
                return weakestPoints + pointsToGive
            }

            val neededPoints = smallestStep * activeMembers.size

            if (remainingPoints < neededPoints)
            {
                val pointsToGive = remainingPoints / activeMembers.size
                return weakestPoints + pointsToGive
            }

            activeMembers.giveToEach(smallestStep)
            weakestPoints += smallestStep

            activeMembers.add(target)
            if (target.isLeaf)
                remainingLeafMembers.remove(target)

            surfaceMembers.add(target)
            surfaceMembers.removeAll(target.children)
        }

        return weakestPoints
    }

    /** Gives [amount] of points from [remainingPoints] to this member and returns its new total points. */
    private fun Member.givePoints(amount: Long): Long
    {
        check(amount >= 0) { "Amount of points to give must be non-negative" }
        check(amount <= remainingPoints) { "Cannot give $amount points to $this, only $remainingPoints points left" }

        log("Giving $amount points to $this [[${remainingPoints - amount}]]")
        points += amount
        remainingPoints -= amount
        return points
    }

    /** Gives each [Member] [amount] of points from [remainingPoints]. */
    private fun Iterable<Member>.giveToEach(amount: Long) = forEach { it.givePoints(amount) }

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

    /**
     * Gives points from [availablePoints] to a parent of [member], so parent's points > [member]'s points.
     * If any points were given, recursively calls this function with parent, until leader. (no more parent)
     * [branchStabilisationPointsNeeded] should be called before this function to check if there are enough points to stabilise the branch.
     * @return remaining points after giving out
     * @throws OutOfPointsException when running out of points without reaching stability
     */
    private fun stabiliseBranch(member: Member, availablePoints: Long): Long
    {
        val parent = member.parent
        var remainingPoints = availablePoints

        if (parent == null)
            return remainingPoints

        if (parent.points <= member.points)
        {
            val neededPoints = member.points - parent.points + 1
            if (remainingPoints < neededPoints)
                throw OutOfPointsException()
            parent.givePoints(neededPoints)
            remainingPoints -= neededPoints

            if (parent.parent != null)
                return stabiliseBranch(parent, remainingPoints)
            else
                return remainingPoints
        }
        else
            return remainingPoints
    }
}

class OutOfPointsException(message: String = "Ran out of points during stabilisation") : RuntimeException(message)