package my.company.config

data class AuthInfo(
    var type: AuthType = AuthType.NONE,
    var tokenHeaderName: String? = null,
    var fieldNameToken: List<String> = listOf()
)