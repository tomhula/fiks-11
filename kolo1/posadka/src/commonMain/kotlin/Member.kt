data class Member(
    val index: Int,
    val parentIndex: Int?,
    var points: Long
)
{
    override fun toString() = "$points${(index + 1).toSuperScriptString()}"

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