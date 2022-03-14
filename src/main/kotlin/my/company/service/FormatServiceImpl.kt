package my.company.service

import my.company.model.AbstractResponse
import my.company.model.LogError
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
        val stringBuilder = baseFormatResponse(logResponse)
        stringBuilder.append("\n<------------------------------>\n")
        return stringBuilder.toString()
    }

    override fun formatError(logError: LogError): String {
        val stringBuilder = baseFormatResponse(logError)
        stringBuilder.append("STACKTRACE: -->\n ${logError.stackTrace}")
        stringBuilder.append("\n<------------------------------>\n")
        return stringBuilder.toString()
    }

    private fun baseFormatResponse(responseModel: AbstractResponse): StringBuilder {
        val stringBuilder = StringBuilder()
        stringBuilder.append("\n------------------------------>>>\n")
        stringBuilder.append("REQUEST-ID: ${responseModel.requestId}\n")
        stringBuilder.append("METHOD: ${responseModel.method}\n")
        stringBuilder.append("URI: ${responseModel.uri}\n")
        stringBuilder.append("DURATION_REQUEST: ${responseModel.duration} ms\n")
        stringBuilder.append("TOKEN_INFO: ${responseModel.tokenInfo}\n")
        stringBuilder.append("HEADERS: ${responseModel.headers}\n")
        stringBuilder.append("BODY: ${responseModel.body}")
        return stringBuilder
    }
}