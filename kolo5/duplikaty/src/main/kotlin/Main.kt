import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

fun main()
{
    val entries = parseInput()

    for (entry in entries)
        println(solve(entry))
}

fun countSubstrings(strings: List<String>): Int
{
    val count = AtomicInteger()
    val appearances = mutableMapOf<String, Int>()

    val stringsHalf = strings.size / 2

    val t1 = thread {
        for (i in 0 ..< stringsHalf)
        {
            val substring = strings[i]
            for ((j, string) in strings.withIndex())
                if (i != j && string.contains(substring))
                    count.incrementAndGet()
        }
    }
    val t2 = thread {
        for (i in stringsHalf ..< strings.size)
        {
            val substring = strings[i]
            for ((j, string) in strings.withIndex())
                if (i != j && string.contains(substring))
                    count.incrementAndGet()
        }
    }
    val t3 = thread {
        for (string in strings)
            appearances[string] = (appearances[string] ?: 0) + 1
    }

    t1.join()
    t2.join()
    t3.join()

    val result = count.get() - appearances.values.sumOf { combinationsOfTwo(it) }

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
