class Crew(
    private val members: Array<Member>,
    availablePoints: Long
)
{
    var remainingPoints = availablePoints
    val leader = members.find { it.parent == null } ?: throw IllegalStateException("No leader found")
    val leafMembers = members.filter { it.children.isEmpty() }.toSet()
    val size = members.size

    fun getMemberByIndex(index: Int) = members[index]

    override fun toString() = "[[$remainingPoints]]\n" + members.joinToString(separator = "\n")

    class Builder(crewSize: Int, private val availablePoints: Long)
    {
        private val preMembers = Array<PreMember?>(crewSize) { null }
        private val preMembersChildren = Array<MutableSet<PreMember>>(crewSize) { mutableSetOf() }
        private lateinit var leader: PreMember

        fun addMember(index: Int, parentIndex: Int, points: Long)
        {
            val preMember = PreMember(index, parentIndex, points)
            preMembers[index] = preMember

            if (preMember.parentIndex >= 0)
                preMembersChildren[parentIndex].add(preMember)
            else
                leader = preMember
        }

        fun build(): Crew
        {
            val preMembers = this.preMembers as Array<PreMember>
            val size = preMembers.size
            val members = arrayOfNulls<Member?>(size)

            fun getOrCreateMember(preMember: PreMember): Member
            {
                val member = members[preMember.index] ?: if (preMember.parentIndex < 0)
                    Member(preMember.index, preMember.points, null, mutableSetOf())
                else
                    members[preMember.index] ?: Member(
                        preMember.index,
                        preMember.points,
                        getOrCreateMember(preMembers[preMember.parentIndex]),
                        mutableSetOf()
                    )

                members[member.index] = member
                return member
            }

            for (preMember in preMembers)
            {
                val member = getOrCreateMember(preMember)
                (member.children as MutableSet).addAll(preMembersChildren[member.index].map { getOrCreateMember(it) })
            }

            return Crew(members as Array<Member>, availablePoints)
        }

        private data class PreMember(val index: Int, val parentIndex: Int, val points: Long)
    }
}

fun buildCrew(crewSize: Int, availablePoint: Long, block: Crew.Builder.() -> Unit) = Crew.Builder(crewSize, availablePoint).apply(block).build()