package my.company.model

abstract class AbstractResponse {
    abstract val requestId: String
    abstract val method: String
    abstract val status: String
    abstract val uri: String
    abstract val headers: List<MutableMap<String, List<String>>>
    abstract val duration: String
    abstract val tokenInfo: String
    abstract val podIp: String
    abstract val body: String
}
