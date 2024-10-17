val logEnabled = System.getenv("LOG") == "true"

fun log(message: () -> Any)
{
    if (logEnabled)
        println(message())
}