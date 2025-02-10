fun main()
{
    val entries = parseInput()

    for (entry in entries)
        println(solve(entry))
}

fun countSubstrings(strings: List<String>): Int
{
    var count = 0
    val appearances = mutableMapOf<String, Int>()

    for ((i, substring) in strings.withIndex())
    {
        appearances.putIfAbsent(substring, 0)
        appearances[substring] = appearances[substring]!! + 1
        for ((j, string) in strings.withIndex())
            if (i != j && string.contains(substring))
                count++
    }

    val result = count - appearances.values.sumOf { combinationsOfTwo(it) }

    return result
}

private fun combinationsOfTwo(n: Int) = n * (n - 1) / 2

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
