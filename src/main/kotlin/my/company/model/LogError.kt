package my.company.model

import java.time.LocalDateTime

data class LogError(
    val requestId: String,
    val method: String,
    val status: String,
    val uri: String,
    val headers: MutableList<String>,
    val userInfo: UserInfo,
    val profile: String,
    val podIp: String,
    val time: LocalDateTime = LocalDateTime.now(),
    val body: String,
    val stackTrace: String
)
