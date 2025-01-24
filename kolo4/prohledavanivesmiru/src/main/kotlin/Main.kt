// -x^{a}\cdot\sin\left(\sqrt{bx}+c\right)
// a=0.477, b=6.624, c=5

fun main()
{
    val numOfInputs = readln().toInt()
    repeat(numOfInputs) {
        val (number1, number2, number3) = readInts()
        val solver = Solver(number1, number2, number3)
        println(solver.solve())
    }
}

fun generate(
    end: Int,
    action: (Int, Vec) -> Unit,
)
{
    var num = 1
    var layer = 1
    var pos = Vec(0, 0)
    var dir = Direction.LEFT

    fun move()
    {
        action(num, pos)
        pos += dir.vec
        num++
    }

    fun makeLayer(layer: Int)
    {
        repeat(3) {
            repeat(layer - 1) {
                move()
            }
            dir = dir.next()
        }
    }


    while (true)
    {
        if (num > end) break
        move()
        layer++
        dir = Direction.RIGHT_UP
        makeLayer(layer)
        if (num > end) break
        move()
        layer++
        dir = Direction.LEFT_UP
        makeLayer(layer)
    }
}
