data class Point(
    val x: Double,
    val y: Double
)
{
    constructor(x: Int, y: Int): this(x.toDouble(), y.toDouble())

    override fun toString() = "[$x, $y]"
}