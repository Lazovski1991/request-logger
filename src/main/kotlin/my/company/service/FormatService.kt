package my.company.service

import my.company.model.LogRequest
import my.company.model.LogResponse

interface FormatService {
    fun formatRequest(logRequest: LogRequest): String
    fun formatResponse(logResponse: LogResponse, enableLogStacktrace: Boolean): String
}