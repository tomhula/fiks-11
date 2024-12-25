fun main()
{
    val spaces = parseInput()

    for (space in spaces)
    {
        val solver = Solver(space)
        val result = solver.solve()
        if (result != null)
            println("${result.x} ${result.y}")
        else
            println("ajajaj")
    }
}

private fun parseInput(): Array<Space>
{
    val entriesCount = readln().toInt()

    return Array<Space>(entriesCount) {
        // Number of stars
        readln()

        val xs = readInts()
        val ys = readInts()

        val stars = xs.zip(ys) { x, y -> Point(x, y) }
        Space(stars.toSet())
    }
}