package my.company.service

interface InfoExtractFromTokenService {
    fun checkOrGetTokenInfo(token: String): String
}