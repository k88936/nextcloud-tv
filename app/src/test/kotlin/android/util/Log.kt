package android.util

object Log {
    const val WTF = 1
    const val VERBOSE = 2
    const val DEBUG = 3
    const val INFO = 4
    const val WARN = 5
    const val ERROR = 6
    const val ASSERT = 7

    var level = ERROR

    @JvmStatic
    fun d(tag: String?, msg: String): Int {
        if (level < DEBUG) return 0
        println("D/$tag: $msg")
        return 0
    }

    @JvmStatic
    fun e(tag: String?, msg: String): Int {
        if (level < ERROR) return 0
        println("E/$tag: $msg")
        return 0
    }

    @JvmStatic
    fun e(tag: String?, msg: String, tr: Throwable?): Int {
        if (level < ERROR) return 0
        println("E/$tag: $msg")
        tr?.let { println("E/$tag: ${it.message}") }
        return 0
    }

    @JvmStatic
    fun w(tag: String?, msg: String): Int {
        if (level < WARN) return 0
        println("W/$tag: $msg")
        return 0
    }

    @JvmStatic
    fun i(tag: String?, msg: String): Int {
        if (level < INFO) return 0
        println("I/$tag: $msg")
        return 0
    }

    @JvmStatic
    fun v(tag: String?, msg: String): Int {
        if (level < VERBOSE) return 0
        println("V/$tag: $msg")
        return 0
    }

    @JvmStatic
    fun wtf(tag: String?, msg: String): Int {
        if (level < WTF) return 0
        println("WTF/$tag: $msg")
        return 0
    }

    @JvmStatic
    fun isLoggable(tag: String?, level: Int): Boolean = true

    @JvmStatic
    fun println(priority: Int, tag: String?, msg: String): Int {
        if (level >= priority) return 0

        val level = when (priority) {
            VERBOSE -> "V"
            DEBUG -> "D"
            INFO -> "I"
            WARN -> "W"
            ERROR -> "E"
            ASSERT -> "A"
            else -> "WTF"
        }
        println("$level/$tag: $msg")
        return 0
    }
}
