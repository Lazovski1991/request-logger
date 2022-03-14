package my.company.model

data class LogRequest(
    val requestId: String,
    val method: String,
    val uri: String,
    val userAgent: String,
    val deviceId: String,
    val token: String,
    val headers: String,
    val params: String,
    var fileUpload: String,
    val tokenInfo: String,
    val requestIp: String,
    val body: String
)