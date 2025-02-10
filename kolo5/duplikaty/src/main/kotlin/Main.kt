fun main()
{
    val entries = parseInput()

    for (entry in entries)
        println(solve(entry))
}

private fun solve(entry: Entry): Int
{
    val textMapSize = entry.totalLength + entry.words.size - 1
    /* Char index in `text` to word index in `entry.words` */
    val textMap = Array(textMapSize) { -1 }
    /* Points to the start of next word */
    var textMapCurrentIndex = 0
    val text = buildString {
        for ((wordIndex, word) in entry.words.withIndex())
        {
            append(word)
            if (wordIndex != entry.words.lastIndex)
                append("|")
            for (charIndex in word.indices)
                textMap[textMapCurrentIndex + charIndex] = wordIndex
            textMapCurrentIndex += word.length + 1
        }
    }

    val ahoCorasick = AhoCorasick(text, entry.words)

    val matches = ahoCorasick.getMatches()

    var actualMatchCount = 0

    /* Word -> Words containing it */
    val actualMatches = mutableMapOf<Int, Set<Int>>()
    /* Word -> Words it contains */
    val reversedActualMatches = mutableMapOf<Int, MutableSet<Int>>()

    for ((wordMatchesIndex, wordMatches) in matches.withIndex())
    {
        val matchedWords = mutableSetOf<Int>()

        for (wordMatch in wordMatches)
        {
            val word = textMap[wordMatch]
            if (matchedWords.add(word))
                reversedActualMatches.getOrPut(word, { mutableSetOf() }).add(wordMatchesIndex)
        }

        actualMatches[wordMatchesIndex] = matchedWords
        actualMatchCount += matchedWords.size
    }

    // println(text)
    // println(actualMatches)
    // println(reversedActualMatches)

    /* Remove self-matches */
    return actualMatchCount - entry.words.size
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
