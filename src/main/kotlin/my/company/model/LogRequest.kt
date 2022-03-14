package my.company.model

data class LogRequest(
    val requestId: String,
    val method: String,
    val uri: String,
    val userAgent: String,
    val deviceId: String,
    val token: String,
    val headers: List<MutableMap<String, List<String>>>,
    val params: List<MutableMap<String, String>>,
    var fileUpload: List<String>,
    val tokenInfo: String,
    val requestIp: String,
    val body: String
)