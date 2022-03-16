package my.company.util

import my.company.util.Constants.STACKTRACE_MDC
import org.slf4j.MDC

object Util {
    //для того чтоб залогировать стектрейс, в хендлере вызываем этот метод
    fun stackTraceToString(e: Throwable, maxLengthStacktrace: Int=10000): String? {
        val sb = StringBuilder()
        for (element in e.stackTrace) {
            sb.append(element.toString())
            sb.append("\n")
        }
        var stack = sb.toString()

        if (stack.length > maxLengthStacktrace) {
            stack = stack.substring(0, maxLengthStacktrace)
        }

        MDC.put(STACKTRACE_MDC, stack)
        return stack
    }
}