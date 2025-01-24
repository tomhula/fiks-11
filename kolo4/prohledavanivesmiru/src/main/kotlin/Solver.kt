import kotlin.math.*

class Solver(
    val num1: Int,
    val num2: Int,
    val num3: Int
)
{
    fun solve(): Int
    {
        val point1 = NumToPos.numToPos(num1)
        val point2 = NumToPos.numToPos(num2)
        val point3 = NumToPos.numToPos(num3)

        val point12DistanceSquared = point1.distanceToSquared(point2)
        val point23DistanceSquared = point2.distanceToSquared(point3)
        val point31DistanceSquared = point3.distanceToSquared(point1)

        if (point12DistanceSquared dneq point23DistanceSquared || point23DistanceSquared dneq point31DistanceSquared || point31DistanceSquared dneq point12DistanceSquared)
            return -1

        val center = (point1 + point2 + point3) / 3f

        if (!isPointOnGrid(center))
            return 0

        return PosToNum.posToNum(center)
    }

    private infix fun Float.deq(other: Float) = abs(this - other) < DISTANCE_FLOAT_TOLERANCE
    private infix fun Float.dneq(other: Float) = !deq(other)

    companion object
    {
        const val DISTANCE_FLOAT_TOLERANCE = 0.01
        val SQRT_3 = sqrt(3f)

        fun isPointOnGrid(point: Vec) =
            point.x % 0.5f == 0f && (point.y / (SQRT_3 / 2f)).let { it == it.toInt().toFloat() }

        fun layer(number: Int) = ceil((1 + sqrt(1 + 24f * number)) / 6).toInt()
        fun layerSize(layer: Int) = 3 * layer - 2
        fun layerMax(layer: Int) = (3 * layer * layer - layer) / 2
        fun layerMin(layerMax: Int, layerSize: Int) = layerMax - layerSize + 1
        fun layerDirection(layer: Int) = if (layer % 2 == 0) 1 else -1
        fun layerMaxX(layer: Int, direction: Int) = (layer - 1) * direction
        fun layerMinX(layerMaxX: Int) = -layerMaxX

        fun hexagonVertexRadiusToSideRadius(vertexRadius: Int) = (SQRT_3 * vertexRadius) / 2f

        object NumToPos
        {
            fun layerLocalNumber(number: Int, layerMin: Int) = number - layerMin + 1
            fun layerPart(layerLocalNumber: Int, layer: Int) = floor(layerLocalNumber / layer.toFloat()).toInt()
            fun numberX(layerMinX: Int, layerDirection: Int, numberLocalX: Float) =
                layerMinX + numberLocalX * layerDirection

            fun numberCircleY(layerPart: Int, layer: Int, layerLocalNumber: Int) = when (layerPart)
            {
                0 -> layerLocalNumber - 1
                1 -> layer - 1
                2 -> layer - (layerLocalNumber - 2 * layer + 2)
                else -> throw IllegalArgumentException("Layer part must be in range 0..2.")
            }

            fun numberY(numberCircleY: Int) = hexagonVertexRadiusToSideRadius(numberCircleY)
            fun numberLocalX(layer: Int, layerPart: Int, layerLocalNumber: Int) = when (layerPart)
            {
                0 -> (layerLocalNumber - 1) / 2f
                1 -> ((layer - 1) / 2f) + (layerLocalNumber - layer)
                2 -> (layer - 1) / 2f + layer + (layerLocalNumber - 2 * layer - 1) / 2f
                else -> throw IllegalArgumentException("Layer part must be in range 0..2.")
            }

            fun numToPos(number: Int): Vec
            {
                val layer = layer(number)
                val layerSize = layerSize(layer)
                val layerMax = layerMax(layer)
                val layerMin = layerMin(layerMax, layerSize)
                val layerDirection = layerDirection(layer)
                val layerLocalNumber = layerLocalNumber(number, layerMin)
                val layerPart = layerPart(layerLocalNumber, layer)
                val layerMaxX = layerMaxX(layer, layerDirection)
                val layerMinX = layerMinX(layerMaxX)
                val numberLocalX = numberLocalX(layer, layerPart, layerLocalNumber)
                val numberX = numberX(layerMinX, layerDirection, numberLocalX)
                val numberCircleY = numberCircleY(layerPart, layer, layerLocalNumber)
                val numberY = numberY(numberCircleY)

                return Vec(numberX, numberY.toFloat())
            }
        }

        object PosToNum
        {
            fun globalLayerPart(angle: Float) = when (angle)
            {
                in 0f..<(Math.PI.toFloat() / 3) -> 0
                in (Math.PI.toFloat() / 3)..<(2 * Math.PI.toFloat() / 3) -> 1
                else -> 2
            }

            fun actualLayerPart(globalLayerPart: Int, layerDirection: Int) = if (layerDirection == -1)
                globalLayerPart
            else
                2 - globalLayerPart

            fun layer(layerPart: Int, pos: Vec): Int
            {
                val controlVector = when (layerPart)
                {
                    0 -> (Math.PI.toFloat() / 6).let { Vec(cos(it), sin(it)) }
                    1 -> (Math.PI.toFloat() / 2).let { Vec(cos(it), sin(it)) }
                    2 -> (5 * Math.PI.toFloat() / 6).let { Vec(cos(it), sin(it)) }
                    else -> throw IllegalArgumentException("Layer part must be in range 0..2.")
                }
                val angleToControl = acos(controlVector.normalize() dot pos.normalize())
                val distanceToControlLineIntersection = pos.magnitude() * cos(angleToControl)
                val vertexRadiusToSideRadius = (2 * SQRT_3 * distanceToControlLineIntersection) / 3
                return round(vertexRadiusToSideRadius).toInt() + 1
            }

            fun layerLocalNumber(layerPart: Int, layerLocalNumberX: Float, layer: Int, layerDirection: Int) = when (layerPart)
            {
                0 -> 2 * layerDirection * layerLocalNumberX + 1
                1 -> layerDirection * layerLocalNumberX + (layer + 1) / 2f
                2 -> 2 * layer - 1 + 2 * (layerDirection * layerLocalNumberX - (layer - 1) / 2f - (layer - 1))
                else -> throw IllegalArgumentException("Layer part must be in range 0..2.")
            }

            fun layerLocalNumberX(pos: Vec, layerMinX: Int): Float = pos.x - layerMinX

            fun posToNum(pos: Vec): Int
            {
                if (pos == Vec.ZERO)
                    return 1

                val angle = pos.angle().toFloat()
                val globalLayerPart = globalLayerPart(angle)
                val layer = layer(globalLayerPart, pos)
                val layerDirection = layerDirection(layer)
                val actualLayerPart = actualLayerPart(globalLayerPart, layerDirection)
                val layerMax = layerMax(layer)
                val layerMin = layerMin(layerMax, layerSize(layer))
                val layerMinX = layerMinX(layerMaxX(layer, layerDirection))
                val layerLocalNumberX = layerLocalNumberX(pos, layerMinX)
                val layerLocalNumber = layerLocalNumber(actualLayerPart, layerLocalNumberX, layer, layerDirection)
                return layerMin + layerLocalNumber.toInt() - 1
            }
        }
    }
}
