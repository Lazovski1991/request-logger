package my.company.service

interface InfoExtractFromTokenService {
    fun getInfoFromToken(token: String): String
}