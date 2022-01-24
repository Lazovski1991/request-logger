package my.company.model

data class LogResponse(
    override val requestId: String,
    override val method: String,
    override val status: String,
    override val uri: String,
    override val headers: List<MutableMap<String, List<String>>>,
    override val userInfo: UserInfo,
    override val profile: String,
    override val podIp: String,
    override val body: String
) : AbstractResponse()
