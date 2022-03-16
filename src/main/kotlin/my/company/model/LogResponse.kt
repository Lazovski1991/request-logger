package my.company.model

data class LogResponse(
    val requestId: String,
    val method: String,
    val status: String,
    val uri: String,
    val headers: String,
    val duration: String,
    val tokenInfo: String,
    val podIp: String,
    val body: String?,
    val stackTrace: String?
)
