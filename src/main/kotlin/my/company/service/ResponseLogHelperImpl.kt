package my.company.service

import my.company.config.LogProperties
import my.company.model.LogError
import my.company.model.LogResponse
import my.company.util.Constants.DURATION_REQUEST_MDC
import my.company.util.Constants.REQUEST_ID_MDC
import my.company.util.Constants.STACKTRACE_MDC
import my.company.util.Constants.TIME_START_REQUEST
import my.company.util.Constants.TOKEN_INFO_MDC
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.Charset
import javax.servlet.http.HttpServletResponse

@Service
class ResponseLogHelperImpl @Autowired constructor(
    val logProperties: LogProperties,
    val formatService: FormatService
) : ResponseLogHelper {
    companion object {
        val logger: Logger = LoggerFactory.getLogger("RESPONSE")
    }

    override fun logResponse(
        request: ContentCachingRequestWrapper,
        response: ContentCachingResponseWrapper
    ) {
        val durationRequest = System.currentTimeMillis() - request.getAttribute(TIME_START_REQUEST).toString().toLong()
        MDC.put(DURATION_REQUEST_MDC, durationRequest.toString())
        if (response.status !in 500..599 || !logProperties.enableLogStacktrace) {
            val logResponse = createLogResponseModel(request, response)
            logger.info(formatService.formatResponse(logResponse))
        } else {
            val logError = createLogResponseModelError(request, response)
            logger.info(formatService.formatError(logError))
        }
    }

    private fun createLogResponseModelError(
        request: ContentCachingRequestWrapper,
        response: ContentCachingResponseWrapper
    ): LogError {
        return LogError(
            MDC.get(REQUEST_ID_MDC),
            request.method,
            response.status.toString(),
            request.requestURI,
            getHeaders(response),
            MDC.get(DURATION_REQUEST_MDC),
            MDC.get(TOKEN_INFO_MDC) ?: "unknown",
            "POD_IP",
            body = getResponseBody(response),
            stackTrace = getStackTrace(),
        )
    }

    private fun createLogResponseModel(
        request: ContentCachingRequestWrapper,
        response: ContentCachingResponseWrapper
    ): LogResponse {
        return LogResponse(
            MDC.get(REQUEST_ID_MDC),
            request.method,
            response.status.toString(),
            request.requestURI,
            getHeaders(response),
            MDC.get(DURATION_REQUEST_MDC),
            MDC.get(TOKEN_INFO_MDC) ?: "unknown",
            "POD_IP",
            body = getResponseBody(response),
        )
    }

    private fun getHeaders(response: ContentCachingResponseWrapper): List<MutableMap<String, List<String>>> {
        val headerNamesList = response.headerNames.toList()
        return headerNamesList.map { nameHeader ->
            val headerValue = response.getHeaders(nameHeader).toList()
            mutableMapOf(nameHeader to headerValue)
        }
    }

    private fun getResponseBody(response: ContentCachingResponseWrapper): String {
        val buf = response.contentAsByteArray
        if (isTypeJson(response) && buf.isNotEmpty()) {
            try {
                return String(buf, 0, buf.size, Charset.forName(response.characterEncoding))
            } catch (e: Exception) {
                RequestLogHelperImpl.logger.error("error in reading request body")
            }
        }
        return ""
    }

    private fun isTypeJson(response: HttpServletResponse): Boolean {
        return (!response.contentType.isNullOrBlank()
                && (response.contentType.contains(MediaType.APPLICATION_JSON_VALUE)
                || response.contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)))
    }

    private fun getStackTrace(): String {
        var trace = MDC.get(STACKTRACE_MDC) ?: "unknown"
        val maxLengthStacktrace = logProperties.lengthStacktrace
        if (trace.length > maxLengthStacktrace) {
            trace = trace.substring(0, maxLengthStacktrace)
        }
        return trace
    }
}