package android.util

object Log {
    const val VERBOSE = 2
    const val DEBUG = 3
    const val INFO = 4
    const val WARN = 5
    const val ERROR = 6
    const val ASSERT = 7

    @JvmStatic
    fun d(tag: String?, msg: String): Int {
        println("D/$tag: $msg")
        return 0
    }

    @JvmStatic
    fun e(tag: String?, msg: String): Int {
        println("E/$tag: $msg")
        return 0
    }

    @JvmStatic
    fun e(tag: String?, msg: String, tr: Throwable?): Int {
        println("E/$tag: $msg")
        tr?.let { println("E/$tag: ${it.message}") }
        return 0
    }

    @JvmStatic
    fun w(tag: String?, msg: String): Int {
        println("W/$tag: $msg")
        return 0
    }

    @JvmStatic
    fun i(tag: String?, msg: String): Int {
        println("I/$tag: $msg")
        return 0
    }

    @JvmStatic
    fun v(tag: String?, msg: String): Int {
        println("V/$tag: $msg")
        return 0
    }

    @JvmStatic
    fun wtf(tag: String?, msg: String): Int {
        println("WTF/$tag: $msg")
        return 0
    }

    @JvmStatic
    fun isLoggable(tag: String?, level: Int): Boolean = true

    @JvmStatic
    fun println(priority: Int, tag: String?, msg: String): Int {
        val level = when (priority) {
            VERBOSE -> "V"
            DEBUG -> "D"
            INFO -> "I"
            WARN -> "W"
            ERROR -> "E"
            ASSERT -> "A"
            else -> "?"
        }
        println("$level/$tag: $msg")
        return 0
    }
}
