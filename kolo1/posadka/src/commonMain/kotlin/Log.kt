val debugEnabled = false

inline fun log(message: () -> Any)
{
    if (debugEnabled)
        println(message())
}