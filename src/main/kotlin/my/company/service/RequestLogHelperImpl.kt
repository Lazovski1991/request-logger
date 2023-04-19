package my.company.service

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.Part
import my.company.config.LogProperties
import my.company.model.LogRequest
import my.company.util.Constants.APPLICATION_NAME
import my.company.util.Constants.DEVICE_ID_HEADER
import my.company.util.Constants.DEVICE_ID_MDC
import my.company.util.Constants.FILE_UPLOAD_MDC
import my.company.util.Constants.METHOD_MDC
import my.company.util.Constants.PARAM_MDC
import my.company.util.Constants.POD_NAME_MDC
import my.company.util.Constants.PROFILE_MDC
import my.company.util.Constants.REQUEST_BODY_MDC
import my.company.util.Constants.REQUEST_HEADERS_MDC
import my.company.util.Constants.REQUEST_ID_HEADER
import my.company.util.Constants.REQUEST_ID_MDC
import my.company.util.Constants.REQUEST_IP_MDC
import my.company.util.Constants.REQUEST_URI_MDC
import my.company.util.Constants.TOKEN_MDC
import my.company.util.Constants.USER_AGENT_MDC
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.util.ContentCachingRequestWrapper
import java.net.InetAddress
import java.nio.charset.Charset
import java.util.*

class RequestLogHelperImpl @Autowired constructor(
    private val logProperties: LogProperties,
    private val infoExtractFromTokenService: InfoExtractFromTokenService,
    private val formatService: FormatService
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
        MDC.put(METHOD_MDC, request.method)
        MDC.put(REQUEST_URI_MDC, request.requestURI)
        MDC.put(USER_AGENT_MDC, request.getHeader(HttpHeaders.USER_AGENT) ?: "unknown")
        MDC.put(DEVICE_ID_MDC, request.getHeader(DEVICE_ID_HEADER) ?: "unknown")
        MDC.put(TOKEN_MDC, getToken(request))
        MDC.put(REQUEST_HEADERS_MDC, getHeaders(request))
        MDC.put(PARAM_MDC, getParam(request))
        MDC.put(FILE_UPLOAD_MDC, if (isIncludeFormData(request)) getPartFileName(request.parts).toString() else "")
        MDC.put(REQUEST_IP_MDC, request.remoteAddr)
        MDC.put(REQUEST_BODY_MDC, getRequestBody(request))
        MDC.put(POD_NAME_MDC, InetAddress.getLocalHost().hostName ?: "unknown")
        log()
    }

    private fun log() {
        val logRequest = LogRequest(
            MDC.get(REQUEST_ID_MDC),
            MDC.get(METHOD_MDC),
            MDC.get(REQUEST_URI_MDC),
            MDC.get(USER_AGENT_MDC),
            MDC.get(DEVICE_ID_MDC),
            MDC.get(POD_NAME_MDC),
            MDC.get(TOKEN_MDC),
            MDC.get(REQUEST_HEADERS_MDC),
            MDC.get(PARAM_MDC),
            MDC.get(FILE_UPLOAD_MDC),
            infoExtractFromTokenService.checkOrGetTokenInfo(MDC.get(TOKEN_MDC)),
            MDC.get(REQUEST_IP_MDC),
            MDC.get(REQUEST_BODY_MDC)
        )
        if (logProperties.enableLogRequest) logger.info(formatService.formatRequest(logRequest))
    }

    private fun getToken(request: ContentCachingRequestWrapper): String {
        var extractToken: String? = null
        if (logProperties.auth.tokenHeaderName != null) {
            extractToken = request.getHeader(logProperties.auth.tokenHeaderName)
        }
        return extractToken ?: "unknown"
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
                return String(buf, 0, buf.size, Charset.forName("UTF-8"))
            } catch (e: Exception) {
                logger.error("error in reading request body")
            }
        }
        return ""
    }

    private fun getHeaders(request: HttpServletRequest): String {
        val headerNamesList = request.headerNames.toList()
        return headerNamesList
            .filter { !it.equals(logProperties.auth.tokenHeaderName, true) }
            .map { nameHeader ->
                val headerValue = request.getHeaders(nameHeader).toList()
                mutableMapOf(nameHeader to headerValue)
            }.toString()
    }

    private fun getParam(request: HttpServletRequest): String {
        val paramNames = request.parameterNames.toList()
        return paramNames.map { paramName ->
            val paramValue = request.getParameter(paramName)
            mutableMapOf(paramName to paramValue)
        }.toString()
    }

    private fun isIncludeFormData(request: HttpServletRequest): Boolean {
        return (!request.contentType.isNullOrBlank())
                && (request.contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE))
    }
}