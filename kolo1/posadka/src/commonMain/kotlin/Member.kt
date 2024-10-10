data class Member(
    val index: Int,
    var points: Long,
    val parent: Member?,
    val children: Set<Member>
)
{
    val isLeader = parent == null
    val isLeaf = children.isEmpty()
    val signature
        get() = "$points${(index + 1).toSuperScriptString()}"

    val distanceToBelowParent
        get() = parent?.points?.minus(points)?.minus(1)

    fun getParent(depth: Int): Member?
    {
        var current = this
        repeat(depth) {
            current = current.parent ?: return null
        }
        return current
    }

    /** Parent depth (1 = direct parent) to the distance */
    fun getFirstParentWithDistance(): Pair<Int, Long?>
    {
        if (parent == null)
            return 1 to null

        var current = this
        var currentDepth = 1
        var currentDistance = distanceToBelowParent

        while(currentDistance == 0L)
        {
            current = current.parent ?: return currentDepth to null
            currentDepth++
            currentDistance = current.distanceToBelowParent
        }

        return currentDepth to currentDistance
    }

    override fun toString() = buildString {

        append(signature)
        if (children.isNotEmpty())
            append("<").append(children.joinToString(separator = ",") { it.signature })
    }

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false
        other as Member
        return index == other.index
    }

    override fun hashCode() = index.hashCode()
}

private fun Int.toSuperScriptString(): String
{
    val superscripts = mapOf(
        '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
        '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹'
    )
    return this.toString().map { superscripts[it] ?: it }.joinToString("")
}