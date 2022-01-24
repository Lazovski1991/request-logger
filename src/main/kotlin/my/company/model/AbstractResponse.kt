package my.company.model

import java.time.LocalDateTime

abstract class AbstractResponse {
    abstract val requestId: String
    abstract val method: String
    abstract val status: String
    abstract val uri: String
    abstract val headers: List<MutableMap<String, List<String>>>
    abstract val userInfo: UserInfo
    abstract val profile: String
    abstract val podIp: String
    val time: LocalDateTime = LocalDateTime.now()
    abstract val body: String
}
