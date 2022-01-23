package my.company.service

import my.company.model.LogError
import my.company.model.LogRequest
import my.company.model.LogResponse

interface FormatService {
    fun formatRequest(logRequest: LogRequest): String

    fun formatResponse(logResponse: LogResponse): String

    fun formatError(logError: LogError): String
}