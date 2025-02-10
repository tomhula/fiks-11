fun main()
{
    val entries = parseInput()

    for (entry in entries)
        println(solve(entry))
}

fun countSubstrings(strings: List<String>): Int
{
    var count = 0
    val uniquePairs = mutableSetOf<Long>()

    for ((i, substring) in strings.withIndex())
        for ((j, string) in strings.withIndex())
            if (i != j && string.contains(substring) && uniquePairs.add(cantorHash(i, j)))
                count++

    return count
}

/* ChatGPT: https://chatgpt.com/share/67a9e3cc-943c-800e-b662-ca7e31f84333  */
private fun cantorHash(a: Int, b: Int): Long
{
    val (x, y) = if (a < b) a to b else b to a
    return ((x + y).toLong() * (x + y + 1) / 2) + x
}

fun primeHash(a: Int, b: Int): Long {
    val P = 150001L
    val Q = 150003L
    val (x, y) = if (a < b) a to b else b to a
    return P * x + Q * y
}

private fun solve(entry: Entry): Int
{
    return countSubstrings(entry.words)
}

private fun parseInput(): List<Entry>
{
    val numOfEntries = readln().toInt()

    val entries = mutableListOf<Entry>()

    repeat(numOfEntries) {
        val numOfWords = readln().toInt()
        val words = mutableListOf<String>()
        var totalLength = 0

        repeat(numOfWords) {
            val word = readln()
            words.add(word)
            totalLength += word.length
        }

        entries.add(Entry(words, totalLength))
    }

    return entries
}
