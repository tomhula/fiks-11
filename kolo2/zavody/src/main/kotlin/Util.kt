fun readInts(): List<Int> = readln().split(" ").map(String::toInt)

fun readLongs(): List<Long> = readln().split(" ").map(String::toLong)

/** Finds the smallest result of [selector] between all elements. And returns the vale and all elements that resulted in it. */
fun <T, V : Comparable<V>> Iterable<T>.allMinByOrNull(selector: (T) -> V): Pair<V, List<T>>?
{
    val iterator = iterator()
    if (!iterator.hasNext())
        return null

    var min = selector(iterator.next())
    val minElements = mutableListOf<T>()

    for (element in iterator)
    {
        val value = selector(element)
        if (value < min)
        {
            min = value
            minElements.clear()
            minElements.add(element)
        }
        else if (value == min)
            minElements.add(element)
    }

    return min to minElements
}