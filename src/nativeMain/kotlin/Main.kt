import kotlin.math.sqrt

fun main()
{
    val entries = processInput()
    entries.map(::solve).forEach(::println)
}

fun processInput(): List<Entry>
{
    val numOfEntries = readln().toInt()
    val entries = mutableListOf<Entry>()

    repeat(numOfEntries)
    {
        val firstLineInts = readln().split(" ").map(String::toInt)
        val numOfPoints = firstLineInts[0]
        val speeds = Speeds(firstLineInts[1], firstLineInts[2], firstLineInts[3])
        val points = mutableListOf<Point>()
        repeat(numOfPoints) {
            points.add(parsePoint(readln()))
        }
        entries.add(Entry(speeds, points))
    }

    return entries
}

fun solve(entry: Entry): Double
{
    var totalTime = 0.0

    for (i in 0 until entry.points.size - 1)
    {
        val current = entry.points[i]
        val next = entry.points[i + 1]

        val distance = current.distanceTo(next)
        val verticalDistance = current.verticalDistanceTo(next)
        val speed = if (verticalDistance > 0) entry.speeds.ascending
                    else if (verticalDistance == 0) entry.speeds.flat
                    else entry.speeds.descending
        val time = distance / speed
        totalTime += time
    }

    return totalTime
}

fun Point.distanceTo(other: Point): Double
{
    val dif = other - this

    val hd = sqrt((dif.x * dif.x + dif.y * dif.y).toDouble())
    val distance = sqrt((hd * hd + dif.z * dif.z))

    return distance
}

fun Point.verticalDistanceTo(other: Point) = other.z - this.z

fun parsePoint(line: String) = line.split(" ").map(String::toInt).let { Point(it[0], it[1], it[2]) }

data class Entry(
    val speeds: Speeds,
    val points: List<Point>
)

data class Speeds(
    val ascending: Int,
    val flat: Int,
    val descending: Int
)

data class Point(
    val x: Int,
    val y: Int,
    val z: Int
)
{
    operator fun minus(other: Point) = Point(this.x - other.x, this.y - other.y, this.z - other.z)
}
