import org.apfloat.Apfloat
import org.apfloat.Apint

class Solver(
    val num1: Apint,
    val num2: Apint,
    val num3: Apint
)
{
    fun solve(): Apint
    {
        if (num1 == num2 || num2 == num3 || num3 == num1)
            return (-1).api

        val point1 = NumToPos.numToPos(num1)
        val point2 = NumToPos.numToPos(num2)
        val point3 = NumToPos.numToPos(num3)

        val point12DistanceSquared = point1.distanceToSquared(point2)
        val point23DistanceSquared = point2.distanceToSquared(point3)
        val point31DistanceSquared = point3.distanceToSquared(point1)

        val isEquilateral =
            point12DistanceSquared feq point23DistanceSquared && point23DistanceSquared feq point31DistanceSquared && point31DistanceSquared feq point12DistanceSquared
        if (!isEquilateral)
            return (-1).api
        val isOrientedAlongYAxis = (point1.y feq point2.y || point2.y feq point3.y || point3.y feq point1.y)
        if (!isOrientedAlongYAxis)
            return (-1).api

        val center = (point1 + point2 + point3) / 3.apf

        if (!isPointOnGrid(center))
            return Apfloat.ZERO

        return PosToNum.posToNum(center)
    }

    companion object
    {
        val FLOAT_TOLERANCE = 0.01.apf
        val SQRT_3 = sqrt(3.api)

        infix fun Apfloat.feq(other: Apfloat) = abs(this - other) < FLOAT_TOLERANCE
        infix fun Apfloat.fneq(other: Apfloat) = !feq(other)

        fun isPointOnGrid(point: Vec) = isOnGrid(point.x, 0.5f.apf, FLOAT_TOLERANCE) && isOnGrid(point.y, SQRT_3 / 2, FLOAT_TOLERANCE)

        private fun isOnGrid(value: Apfloat, step: Apfloat, error: Apfloat): Boolean
        {
            val nearestGridPoint = round(value / step) * step
            return abs(value - nearestGridPoint) <= error
        }

        fun layer(number: Apint) = ceil((1 + sqrt(1 + 24 * number)) / 6)
        fun layerSize(layer: Apint) = 3 * layer - 2
        fun layerMax(layer: Apint) = (3 * layer * layer - layer) / 2
        fun layerMin(layerMax: Apint, layerSize: Apint) = layerMax - layerSize + 1
        fun layerDirection(layer: Apint) = if ((layer % 2).isZero) API_ONE else (-1).api
        fun layerMaxX(layer: Apint, direction: Apint) = (layer - 1) * direction
        fun layerMinX(layerMaxX: Apint) = -layerMaxX

        fun hexagonVertexRadiusToSideRadius(vertexRadius: Apint) = (SQRT_3 * vertexRadius) / 2

        object NumToPos
        {
            fun layerLocalNumber(number: Apint, layerMin: Apint) = number - layerMin + 1
            fun layerPart(layerLocalNumber: Apint, layer: Apint) = when
            {
                layerLocalNumber < layer -> API_ZERO
                layerLocalNumber < layer * 2 - 1 -> API_ONE
                else -> 2.api
            }
            fun numberX(layerMinX: Apint, layerDirection: Apint, numberLocalX: Apfloat) =
                layerMinX + numberLocalX * layerDirection

            fun numberCircleY(layerPart: Apint, layer: Apint, layerLocalNumber: Apint) = when (layerPart)
            {
                API_ZERO -> layerLocalNumber - 1
                API_ONE -> layer - 1
                2.api -> layer - (layerLocalNumber - 2 * layer + 2)
                else -> throw IllegalArgumentException("Layer part must be in range 0..2.")
            }

            fun numberY(numberCircleY: Apint) = hexagonVertexRadiusToSideRadius(numberCircleY)

            fun numberLocalX(layer: Apint, layerPart: Apint, layerLocalNumber: Apint) = when (layerPart)
            {
                API_ZERO -> (layerLocalNumber - 1) / 2f
                API_ONE -> ((layer - 1) / 2f) + (layerLocalNumber - layer)
                2.api -> (layer - 1) / 2f + layer + (layerLocalNumber - 2 * layer - 1) / 2f
                else -> throw IllegalArgumentException("Layer part must be in range 0..2. (was $layerPart)")
            }

            fun numToPos(number: Apint): Vec
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

                return Vec(numberX, numberY)
            }
        }

        object PosToNum
        {
            fun globalLayerPart(angle: Apfloat) = when (angle)
            {
                in Apfloat.ZERO..<(APF_PI / 3) -> API_ZERO
                in (APF_PI / 3)..<(2 * APF_PI / 3) -> API_ONE
                else -> 2.api
            }

            fun actualLayerPart(globalLayerPart: Apint, layerDirection: Apint) = if (layerDirection == (-1).api)
                globalLayerPart
            else
                2 - globalLayerPart

            fun layer(layerPart: Apint, pos: Vec): Apint
            {
                val controlVector = when (layerPart)
                {
                    0.api -> (APF_PI / 6).let { Vec(cos(it), sin(it)) }
                    1.api -> (APF_PI / 2).let { Vec(cos(it), sin(it)) }
                    2.api -> (5 * APF_PI / 6).let { Vec(cos(it), sin(it)) }
                    else -> throw IllegalArgumentException("Layer part must be in range 0..2.")
                }
                val angleToControl = acos(controlVector.normalize() dot pos.normalize())
                val magnitude = pos.magnitude()
                val cos = cos(angleToControl)
                val distanceToControlLineIntersection = magnitude.multiply(cos)
                val vertexRadiusToSideRadius = (2 * SQRT_3 * distanceToControlLineIntersection) / 3f
                return round(vertexRadiusToSideRadius) + 1
            }

            fun layerLocalNumber(layerPart: Apint, layerLocalNumberX: Apfloat, layer: Apint, layerDirection: Apint) = when (layerPart)
            {
                Apfloat.ZERO -> 2L * layerDirection * layerLocalNumberX + 1
                1.api -> layerDirection * layerLocalNumberX + (layer + 1) / 2f
                2.api -> 2L * layer - 1 + 2L * (layerDirection * layerLocalNumberX - (layer - 1) / 2f - (layer - 1))
                else -> throw IllegalArgumentException("Layer part must be in range 0..2.")
            }

            fun layerLocalNumberX(pos: Vec, layerMinX: Apint): Apfloat = pos.x - layerMinX

            fun posToNum(pos: Vec): Apint
            {
                if (pos == Vec.ZERO)
                    return 1.api

                val angle = pos.angle()
                val globalLayerPart = globalLayerPart(angle)
                val layer = layer(globalLayerPart, pos)
                val layerDirection = layerDirection(layer)
                val actualLayerPart = actualLayerPart(globalLayerPart, layerDirection)
                val layerMax = layerMax(layer)
                val layerMin = layerMin(layerMax, layerSize(layer))
                val layerMinX = layerMinX(layerMaxX(layer, layerDirection))
                val layerLocalNumberX = layerLocalNumberX(pos, layerMinX)
                val layerLocalNumber = layerLocalNumber(actualLayerPart, layerLocalNumberX, layer, layerDirection)
                return layerMin + floor(layerLocalNumber) - 1
            }
        }
    }
}
