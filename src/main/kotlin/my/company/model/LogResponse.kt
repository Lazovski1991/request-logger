package my.company.model

data class LogResponse(
    val requestId: String,
    val method: String,
    val status: String,
    val marker: String,
    val uri: String,
    val duration: String,
    val tokenInfo: String,
    val podName: String,
    val body: String?,
    val stackTrace: String?
)
