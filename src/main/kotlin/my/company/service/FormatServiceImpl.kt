package my.company.service

import my.company.model.LogError
import my.company.model.LogRequest
import my.company.model.LogResponse
import org.springframework.stereotype.Service

@Service
class FormatServiceImpl : FormatService {
    override fun formatRequest(logRequest: LogRequest): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\n------------------------------\n")
        stringBuilder.append("REQUEST!!!!!\n")
        stringBuilder.append("REQUEST-ID: ${logRequest.requestId}\n")
        stringBuilder.append("METHOD: ${logRequest.method}\n")
        stringBuilder.append("URI: ${logRequest.uri}\n")
        stringBuilder.append("USER_AGENT: ${logRequest.userAgent}\n")
        stringBuilder.append("DEVICE-ID: ${logRequest.deviceId}\n")
        stringBuilder.append("AUTH-TOKEN: ${logRequest.token}\n")
        stringBuilder.append("HEADERS: ${logRequest.headers}\n")
        stringBuilder.append("PARAMS: ${logRequest.params}\n")
        stringBuilder.append("FILE_NAME_UPLOAD: ${logRequest.fileUpload}\n")
        stringBuilder.append("USER: email=${logRequest.userInfo.email}, username=${logRequest.userInfo.userName}\n")
        stringBuilder.append("REQUEST-IP: ${logRequest.requestIp}\n")
        stringBuilder.append("PROFILE: ${logRequest.profile}\n")
        stringBuilder.append("TIME: ${logRequest.time}\n")
        stringBuilder.append("BODY: \n${logRequest.body}")
        stringBuilder.append("\n------------------------------\n")
        return stringBuilder.toString()
    }

    override fun formatResponse(logResponse: LogResponse): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\n------------------------------\n")
        stringBuilder.append("RESPONSE!!!!!\n")
        stringBuilder.append("REQUEST-ID: ${logResponse.requestId}\n")
        stringBuilder.append("METHOD: ${logResponse.method}\n")
        stringBuilder.append("URI: ${logResponse.uri}\n")
        stringBuilder.append("HEADERS: ${logResponse.headers}\n")
        stringBuilder.append("USER: email=${logResponse.userInfo.email}, username=${logResponse.userInfo.userName}\n")
        stringBuilder.append("PROFILE: ${logResponse.profile}\n")
        stringBuilder.append("TIME: ${logResponse.time}\n")
        stringBuilder.append("BODY: ${logResponse.body}")
        stringBuilder.append("\n------------------------------\n")
        return stringBuilder.toString()
    }

    override fun formatError(logError: LogError): String {
        TODO("Not yet implemented")
    }
}