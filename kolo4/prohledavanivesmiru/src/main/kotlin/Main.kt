import org.apfloat.Apint

fun main()
{
    val numOfInputs = readln().toInt()
    repeat(numOfInputs) { i ->
        val (number1, number2, number3) = readln().split(" ").map { Apint(it) }
        val solver = Solver(number1, number2, number3)
        println(solver.solve())
    }
}
