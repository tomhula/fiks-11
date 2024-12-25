class Solver(private val space: Space)
{
    /* Can be optimized by calculating the average during parsing */
    fun solve(): Point?
    {
        var xSum = 0.0
        var ySum = 0.0

        for (star in space.stars)
        {
            xSum += star.x
            ySum += star.y
        }

        var xAvg = xSum / space.starCount
        var yAvg = ySum / space.starCount

        var averagePoint = Point(xAvg, yAvg)

        var averagePointIsSymmetryCenter = true

        for (star in space.stars)
        {
            val projection = space.stars.findProjection(star, averagePoint)

            if (projection == null)
            {
                averagePointIsSymmetryCenter = false
                break
            }
        }

        return if (averagePointIsSymmetryCenter)
            averagePoint
        else
            null
    }

    fun Iterable<Point>.findProjection(point: Point, mirror: Point): Point?
    {
        val projection = reflectPointAcrossCenter(point, mirror)

        return if (projection in this)
            projection
        else
            null
    }

    fun reflectPointAcrossCenter(point: Point, center: Point): Point
    {
        val reflectedX = 2 * center.x - point.x
        val reflectedY = 2 * center.y - point.y
        return Point(reflectedX, reflectedY)
    }
}