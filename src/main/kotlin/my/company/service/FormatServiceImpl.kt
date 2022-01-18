package my.company.service

import my.company.model.LogRequest
import org.springframework.stereotype.Service

@Service
class FormatServiceImpl: FormatService {
    override fun logRequestWithPayload(logRequest: LogRequest):String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\n------------------------------\n")

        stringBuilder.append("REQUEST-ID: ${logRequest.requestId}\n")
        stringBuilder.append("METHOD: ${logRequest.method}\n")
        stringBuilder.append("URI: ${logRequest.uri}\n")
        stringBuilder.append("USER_AGENT: ${logRequest.userAgent}\n")
        stringBuilder.append("DEVICE-ID: ${logRequest.deviceId}\n")
        stringBuilder.append("AUTH-TOKEN: ${logRequest.token}\n")
        stringBuilder.append("HEADERS: ${logRequest.headers}\n")
        stringBuilder.append("PARAMS: ${logRequest.params}\n")
        stringBuilder.append("USER: email=${logRequest.userInfo.email}, username=${logRequest.userInfo.userName}\n")
        stringBuilder.append("REQUEST-IP: ${logRequest.requestIp}\n")
        stringBuilder.append("PROFILE: ${logRequest.profile}\n")
        stringBuilder.append("TIME: ${logRequest.time}\n")
        stringBuilder.append("BODY: \n${logRequest.body}\n")
        stringBuilder.append("------------------------------\n")


        return stringBuilder.toString()
    }
}