enum class Direction(val vec: Vec)
{
    LEFT(Vec(-1f, 0f)),
    RIGHT(Vec(1f, 0f)),
    LEFT_DOWN(Vec(-0.5f, -1f)),
    LEFT_UP(Vec(-0.5f, 1f)),
    RIGHT_DOWN(Vec(0.5f, -1f)),
    RIGHT_UP(Vec(0.5f, 1f));

    fun next() = when (this) {
        RIGHT_UP -> RIGHT
        RIGHT -> RIGHT_DOWN
        LEFT_UP -> LEFT
        LEFT -> LEFT_DOWN
        RIGHT_DOWN -> RIGHT
        LEFT_DOWN -> LEFT
    }
}
