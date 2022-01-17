package my.company.model

import java.time.LocalDateTime

data class LogRequest(
    val requestId: String,
    val method: String,
    val uri: String,
    val headers: List<MutableMap<String, List<String>>>,
    val params: List<MutableMap<String, String>>,
    val userInfo: UserInfo,
    val profile: String?,
    val time: LocalDateTime = LocalDateTime.now(),
    val body: String
)