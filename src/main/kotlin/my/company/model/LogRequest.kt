package my.company.model

import java.time.LocalDateTime

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
    val userInfo: UserInfo,
    val requestIp: String,
    val profile: String?,
    val time: LocalDateTime = LocalDateTime.now(),
    val body: String
)