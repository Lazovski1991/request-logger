package my.company.model

data class LogError(
    override val requestId: String,
    override val method: String,
    override val status: String,
    override val uri: String,
    override val headers: List<MutableMap<String, List<String>>>,
    override val duration: String,
    override val tokenInfo: String,
    override val podIp: String,
    override val body: String,
    val stackTrace: String
) : AbstractResponse()
