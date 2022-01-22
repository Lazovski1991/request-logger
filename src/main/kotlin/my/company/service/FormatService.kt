package my.company.service

import my.company.model.LogRequest

interface FormatService {
    fun formatRequest(logRequest: LogRequest):String
}