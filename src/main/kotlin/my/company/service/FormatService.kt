package my.company.service

import my.company.model.LogRequest

interface FormatService {
    fun logRequestWithPayload(logRequest: LogRequest):String
}