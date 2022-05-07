package my.company.config

data class AuthInfo(
    var type: AuthType = AuthType.JWT,
    var tokenHeaderName: String? = null,
    var fieldNameToken: List<String> = listOf()
)