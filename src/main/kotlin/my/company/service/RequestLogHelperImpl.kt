package my.company.service

import my.company.config.LogProperties
import my.company.jwtparselib.service.ParseTokenUtilService
import my.company.model.LogRequest
import my.company.model.UserInfo
import my.company.util.Constants.APPLICATION_NAME
import my.company.util.Constants.DEVICE_ID_HEADER
import my.company.util.Constants.PROFILE_MDC
import my.company.util.Constants.REQUEST_ID_HEADER
import my.company.util.Constants.REQUEST_ID_MDC
import my.company.util.Constants.TIME_START_REQUEST
import my.company.util.Constants.USER_EMAIL_MDC
import my.company.util.Constants.USER_USERNAME_MDC
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.util.ContentCachingRequestWrapper
import java.nio.charset.Charset
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.Part

@Service
class RequestLogHelperImpl @Autowired constructor(
    val logProperties: LogProperties,
    val jwtParse: ParseTokenUtilService,
    val formatService: FormatService
) : RequestLogHelper {
    @Value("\${spring.profiles.active: unknown}")
    private val profile: String = "unknown"

    @Value("\${spring.application.name:unknown}")
    private val applicationName: String = "unknown"

    companion object {
        val logger: Logger = LoggerFactory.getLogger("REQUEST")
    }

    override fun logRequest(request: ContentCachingRequestWrapper) {
        var requestId = request.getHeader(REQUEST_ID_HEADER)
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString()
        }

        MDC.put(REQUEST_ID_MDC, requestId)
        MDC.put(PROFILE_MDC, profile)
        MDC.put(APPLICATION_NAME, applicationName)

        val token = request.getHeader(logProperties.tokenHeaderName)

        val userInfo = UserInfo()
        if (token != null) {
            userInfo.email = jwtParse.getValueFieldFromToken(token, "email")
            userInfo.userName = jwtParse.getValueFieldFromToken(token, "username")
            MDC.put(USER_EMAIL_MDC, userInfo.email)
            MDC.put(USER_USERNAME_MDC, userInfo.userName)
        }

        val logRequest = LogRequest(
            requestId,
            applicationName,
            request.method,
            request.requestURI,
            request.getHeader(HttpHeaders.USER_AGENT),
            request.getHeader(DEVICE_ID_HEADER) ?: "unknown",
            request.getHeader(logProperties.tokenHeaderName) ?: "unknown",
            getHeaders(request),
            getParam(request),
            if (isIncludeFormData(request)) getPartFileName(request.parts) else listOf(),
            userInfo = userInfo,
            requestIp = request.remoteAddr,
            profile = profile,
            body = getRequestBody(request)
        )
        if (logProperties.enableLogRequest) logger.info(formatService.formatRequest(logRequest))
    }

    private fun getPartFileName(parts: MutableCollection<Part>): List<String> {
        val fileName = mutableListOf<String>()
        logProperties.filePartType.forEach {
            parts.filter { part -> part.contentType == it }.map { fileName.add(it.submittedFileName) }
        }
        return fileName
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

    private fun isIncludeFormData(request: HttpServletRequest): Boolean {
        return (!request.contentType.isNullOrBlank())
                && (request.contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE))
    }
}