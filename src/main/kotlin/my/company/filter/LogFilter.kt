package my.company.filter

import my.company.config.LogProperties
import my.company.model.LogRequest
import my.company.model.UserInfo
import my.company.service.FormatService
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.Charset
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LogFilter constructor(
    private val logProperties: LogProperties,
    private val formatService: FormatService
) : OncePerRequestFilter() {
    companion object {
        const val REQUEST_ID_HEADER: String = "Request-Id"
        const val TOKEN_HEADER: String = "AuthorizationToken"
        const val REQUEST_ID_MDC: String = "RequestId"
        const val DEVICE_ID_HEADER: String = "Device-Id"
    }

    @Value("\${spring.profiles.active}")
    private val profile: String = "unknown"

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (logProperties.urlExclude.contains(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        val wrappedRequest = ContentCachingRequestWrapper(request)
        filterChain.doFilter(wrappedRequest, response)

        if (isPayload(request) && request !is ContentCachingResponseWrapper) {
            val logRequestModel = createRequestModel(wrappedRequest)
            logger.info(formatService.logRequestWithPayload(logRequestModel))
        }
    }

    private fun createRequestModel(request: ContentCachingRequestWrapper): LogRequest {
        var requestId = request.getHeader(REQUEST_ID_HEADER)
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString()
        }
        MDC.put(REQUEST_ID_MDC, requestId)

        val userInfo = UserInfo()
        if (request.getHeader(TOKEN_HEADER) != null) {
            //todo распарсить токен и взять что нужно
        }

        return LogRequest(
            requestId,
            request.method,
            request.requestURI,
            request.getHeader(HttpHeaders.USER_AGENT),
            request.getHeader(DEVICE_ID_HEADER) ?: "unknown",
            request.getHeader(TOKEN_HEADER) ?: "unknown",
            getHeaders(request),
            getParam(request),
            userInfo,
            request.remoteAddr,
            profile,
            body = getRequestBody(request)
        )
    }

    private fun isPayload(request: HttpServletRequest): Boolean {
        val contentType = request.contentType
        return (!contentType.isNullOrBlank())
                && (contentType.contains(MediaType.APPLICATION_JSON_VALUE)
                || contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    }

    private fun getRequestBody(request: ContentCachingRequestWrapper): String {
        val buf = request.contentAsByteArray
        if (buf.isNotEmpty()) {
            try {
                return String(buf, 0, buf.size, Charset.forName(request.characterEncoding))
            } catch (e: Exception) {
                logger.error("error in reading request body")
            }
        }
        return ""
    }

    private fun getHeaders(request: HttpServletRequest): List<MutableMap<String, List<String>>> {
        val headerNamesList = request.headerNames.toList()
        return headerNamesList.map { nameHeader ->
            val headerValue = request.getHeaders(nameHeader).toList()
            mutableMapOf(nameHeader to headerValue)
        }
    }

    private fun getParam(request: HttpServletRequest): List<MutableMap<String, String>> {
        val paramNames = request.parameterNames.toList()
        return paramNames.map { paramName ->
            val paramValue = request.getParameter(paramName)
            mutableMapOf(paramName to paramValue)
        }
    }
}