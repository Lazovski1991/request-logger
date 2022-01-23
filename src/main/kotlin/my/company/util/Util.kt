package my.company.util

import my.company.util.Constants.STACKTRACE_MDC
import org.slf4j.MDC

object Util {
    fun stackTraceToString(e: Throwable): String? {
        val sb = StringBuilder()
        for (element in e.stackTrace) {
            sb.append(element.toString())
            sb.append("\n")
        }
        val stack = sb.toString()
        MDC.put(STACKTRACE_MDC, stack)
        return stack
    }
}