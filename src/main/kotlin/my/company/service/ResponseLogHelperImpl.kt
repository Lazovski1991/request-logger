package my.company.service

import my.company.model.LogResponse
import my.company.util.Constants.DURATION_REQUEST_MDC
import my.company.util.Constants.METHOD_MDC
import my.company.util.Constants.POD_IP_MDC
import my.company.util.Constants.REQUEST_ID_MDC
import my.company.util.Constants.REQUEST_URI_MDC
import my.company.util.Constants.RESPONSE_BODY_MDC
import my.company.util.Constants.RESPONSE_HEADERS_MDC
import my.company.util.Constants.RESPONSE_STATUS_MDC
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
        MDC.put(DURATION_REQUEST_MDC, "$durationRequest ms")
        MDC.put(RESPONSE_STATUS_MDC, response.status.toString())
        MDC.put(RESPONSE_HEADERS_MDC, getHeaders(response))
        MDC.put(POD_IP_MDC, "POD_IP")//todo
        MDC.put(RESPONSE_BODY_MDC, getResponseBody(response))

        val logResponse = createLogResponseModel()
        logger.info(formatService.formatResponse(logResponse))
        MDC.clear()
    }

    private fun createLogResponseModel(): LogResponse {
        val logResponse = LogResponse(
            MDC.get(REQUEST_ID_MDC),
            MDC.get(METHOD_MDC),
            MDC.get(RESPONSE_STATUS_MDC),
            MDC.get(REQUEST_URI_MDC),
            MDC.get(RESPONSE_HEADERS_MDC),
            MDC.get(DURATION_REQUEST_MDC),
            MDC.get(TOKEN_INFO_MDC) ?: "unknown",
            MDC.get(POD_IP_MDC),
            MDC.get(RESPONSE_BODY_MDC),
            MDC.get(STACKTRACE_MDC)
        )
        MDC.getCopyOfContextMap()
        return logResponse
    }

    private fun getHeaders(response: ContentCachingResponseWrapper): String {
        val headerNamesList = response.headerNames.toList()
        return headerNamesList.map { nameHeader ->
            val headerValue = response.getHeaders(nameHeader).toList()
            mutableMapOf(nameHeader to headerValue)
        }.toString()
    }

    private fun getResponseBody(response: ContentCachingResponseWrapper): String? {
        val buf = response.contentAsByteArray
        if (isTypeJson(response) && buf.isNotEmpty()) {
            try {
                return String(buf, 0, buf.size, Charset.forName(response.characterEncoding))
            } catch (e: Exception) {
                RequestLogHelperImpl.logger.error("error in reading request body")
            }
        }
        return null
    }

    private fun isTypeJson(response: HttpServletResponse): Boolean {
        return (!response.contentType.isNullOrBlank()
                && (response.contentType.contains(MediaType.APPLICATION_JSON_VALUE)
                || response.contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)))
    }
}