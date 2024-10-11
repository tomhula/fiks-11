class Crew(
    val members: Array<Member>,
    val membersSorted: Array<Member>,
    val leafMembersMask: BooleanArray,
    var availablePoints: Long,
)
{
    val size = members.size

    fun getParentOf(member: Member) = member.parentIndex?.let { members[member.parentIndex] }

    fun isLeaf(member: Member) = leafMembersMask[member.index]

    fun getDistanceToBelowParent(member: Member): Long?
    {
        val parent = getParentOf(member) ?: return null
        return parent.points - member.points - 1
    }

    override fun toString() = "[[$availablePoints]]\n" + members.joinToString(separator = "\n")
}