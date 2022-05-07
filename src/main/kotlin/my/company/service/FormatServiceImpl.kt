package my.company.service

import my.company.model.LogRequest
import my.company.model.LogResponse
import org.springframework.stereotype.Service

@Service
class FormatServiceImpl : FormatService {
    override fun formatRequest(logRequest: LogRequest): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\n------------------------------>\n")
        stringBuilder.append("REQUEST-ID: ${logRequest.requestId}\n")
        stringBuilder.append("METHOD: ${logRequest.method}\n")
        stringBuilder.append("URI: ${logRequest.uri}\n")
        stringBuilder.append("USER_AGENT: ${logRequest.userAgent}\n")
        stringBuilder.append("DEVICE-ID: ${logRequest.deviceId}\n")
        stringBuilder.append("POD_NAME: ${logRequest.podName}\n")
        stringBuilder.append("AUTH-TOKEN: ${logRequest.token}\n")
        stringBuilder.append("TOKEN_INFO: ${logRequest.tokenInfo}\n")
        stringBuilder.append("HEADERS: ${logRequest.headers}\n")
        stringBuilder.append("PARAMS: ${logRequest.params}\n")
        stringBuilder.append("FILE_NAME_UPLOAD: ${logRequest.fileUpload}\n")
        stringBuilder.append("REQUEST-IP: ${logRequest.requestIp}\n")
        stringBuilder.append("BODY: \n${logRequest.body}")
        stringBuilder.append("\n<------------------------------>\n")
        return stringBuilder.toString()
    }

    override fun formatResponse(logResponse: LogResponse): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\n------------------------------>>>\n")
        stringBuilder.append("REQUEST-ID: ${logResponse.requestId}\n")
        stringBuilder.append("METHOD: ${logResponse.method}\n")
        stringBuilder.append("STATUS: ${logResponse.status}\n")
        stringBuilder.append("URI: ${logResponse.uri}\n")
        stringBuilder.append("DURATION_REQUEST: ${logResponse.duration}\n")
        stringBuilder.append("TOKEN_INFO: ${logResponse.tokenInfo}\n")
        stringBuilder.append("POD_NAME: ${logResponse.podName}\n")

        if (logResponse.body != null)
            stringBuilder.append("BODY: ${logResponse.body}")

        if (logResponse.stackTrace != null)
            stringBuilder.append("\nSTACKTRACE: -->\n ${logResponse.stackTrace}")

        stringBuilder.append("\n<------------------------------>\n")
        return stringBuilder.toString()
    }
}