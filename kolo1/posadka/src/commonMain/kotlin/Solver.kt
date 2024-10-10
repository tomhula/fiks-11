class Solver(val crew: Crew)
{
    private val leafMembersSorted = crew.leafMembers.sortedBy { it.points }.toMutableList()
    private val weakestPoints
        get() = leafMembersSorted.first().points
    private val weakestMembers = leafMembersSorted.takeWhile { it.points == weakestPoints }.toMutableSet()
    private val weakestMembersCount
        get() = weakestMembers.size

    private val secondWeakestPoints
        get() = leafMembersSorted.drop(weakestMembers.size).firstOrNull()?.points
    private var secondWeakestMembers =
        leafMembersSorted.drop(weakestMembers.size).takeWhile { it.points == secondWeakestPoints }.toMutableSet()
    private val secondWeakestMembersCount
        get() = secondWeakestMembers.size

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

        if (crew.size == 1)
        {
            crew.leader.givePoints(crew.remainingPoints)
            return crew.leader.points
        }

        while (true)
        {
            log("CREW: ")
            log(crew)
            log("weakestMembers: $weakestMembers")

            val secondWeakestMemberExists = secondWeakestMembers.isNotEmpty()

            if (secondWeakestMemberExists)
            {
                log("### Second weakest member exists ($secondWeakestMembers)")
                val catchupDistance = secondWeakestPoints!! - weakestPoints
                val pointsNeeded = branchStabilisationPointsNeeded(weakestMembers, catchupDistance) + catchupDistance * weakestMembersCount
                val catchupPossible = pointsNeeded <= remainingPoints

                log("catchupDistance: $catchupDistance")

                if (catchupPossible)
                {
                    log("### Catchup possible")
                    weakestMembers.giveToEachAndStabilize(catchupDistance)
                    weakestMembers.addAll(secondWeakestMembers)
                    recalSecondWeakestMembers()
                    continue
                }
            }

            log("### Second weakest member does not exist")
            val smallestStep = findSmallestStep()

            if (smallestStep != null)
            {
                log("### Smallest step exists")
                val combinedNestLevels = weakestMembers.sumOf { it.getFirstParentWithDistance().first }
                val toSub = findNearParentsOccurrenceCount(weakestMembers).values.sumOf { it - 1 }
                val pointsNeeded = (combinedNestLevels - toSub) * smallestStep
                val stepPossible = pointsNeeded <= remainingPoints

                log("smallestStep: $smallestStep")

                if (stepPossible)
                {
                    log("### Step possible")
                    weakestMembers.giveToEachAndStabilize(smallestStep)
                    continue
                }
                else
                {
                    log("### Step not possible")
                }
            }
            else
            {
                log("### Smallest step does not exist")
            }

            val combinedNestLevels = weakestMembers.sumOf { it.getFirstParentWithDistance().first }
            val toSub = findNearParentsOccurrenceCount(weakestMembers).values.sumOf { it - 1 }
            val pointsToGive = remainingPoints / (combinedNestLevels - toSub)
            weakestMembers.giveToEachAndStabilize(pointsToGive)
            break
        }

        log("FINAL CREW: ")
        log(crew)

        return leafMembersSorted.first().points
    }

    private fun findNearParentsOccurrenceCount(members: Iterable<Member>): Map<Member, Int>
    {
        val parents = mutableMapOf<Member, Int>()

        for (member in members)
        {
            var currentMember = member

            while (true)
            {
                val currentParent = currentMember.parent ?: break
                if (currentMember.distanceToBelowParent != 0L)
                    break

                parents[currentParent] = (parents[currentParent] ?: 0) + 1
                currentMember = currentParent
            }
        }

        return parents
    }

    private fun findSmallestStep(): Long?
    {
        var smallestStep: Long? = null

        for (weakestMember in weakestMembers)
        {
            val step = weakestMember.getFirstParentWithDistance().second ?: continue
            if (smallestStep == null || step < smallestStep)
                smallestStep = step
        }

        return smallestStep
    }

    private fun recalSecondWeakestMembers()
    {
        val secondWeakestPoints = leafMembersSorted.drop(weakestMembersCount).firstOrNull()?.points
        if (secondWeakestPoints != null)
            secondWeakestMembers =
                leafMembersSorted.drop(weakestMembersCount).takeWhile { it.points == secondWeakestPoints }
                    .toMutableSet()
        else
            secondWeakestMembers = mutableSetOf()
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

    private fun Member.givePointsAndStabilise(amount: Long)
    {
        givePoints(amount)
        remainingPoints = stabiliseBranch(this, remainingPoints)
    }

    private fun Iterable<Member>.giveToEachAndStabilize(amount: Long) = forEach { it.givePointsAndStabilise(amount) }

    /** Gives each [Member] [amount] of points from [remainingPoints]. */
    private fun Iterable<Member>.giveToEach(amount: Long) = forEach { it.givePoints(amount) }

    private fun branchStabilisationPointsNeeded(member: Member, memberPointsOffset: Long = 0): Long
    {
        var totalPointsNeeded = 0L
        var currentMember = member
        var currentOffset = memberPointsOffset

        while (true)
        {
            val parent = currentMember.parent ?: break

            val parentPoints = parent.points
            val memberPoints = currentMember.points + currentOffset

            if (parentPoints <= memberPoints)
            {
                val neededPoints = memberPoints - parentPoints + 1
                totalPointsNeeded += neededPoints
                currentMember = parent
                currentOffset = neededPoints
            }
            else
                break
        }

        return totalPointsNeeded
    }

    private fun branchStabilisationPointsNeeded(members: Iterable<Member>, memberPointsOffset: Long = 0): Long
    {
        var totalPointsNeeded = 0L

        val offsets = members.associateWith { memberPointsOffset }.toMutableMap()

        for (member in members)
        {
            var currentMember = member

            while (true)
            {
                val parent = currentMember.parent ?: break

                val parentPoints = parent.points + offsets.getOrElse(parent) { 0 }
                val memberPoints = currentMember.points + offsets.getOrElse(member) { 0 }

                if (parentPoints <= memberPoints)
                {
                    val neededPoints = memberPoints - parentPoints + 1
                    totalPointsNeeded += neededPoints
                    offsets[parent] = (offsets[parent] ?: 0) + neededPoints
                    currentMember = parent
                }
                else
                    break
            }
        }

        return totalPointsNeeded
    }

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