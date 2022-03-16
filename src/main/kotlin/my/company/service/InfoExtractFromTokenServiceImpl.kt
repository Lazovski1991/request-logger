package my.company.service

import my.company.config.LogProperties
import my.company.jwtparselib.service.ParseTokenUtilService
import my.company.model.TokenInfo
import my.company.util.Constants
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class InfoExtractFromTokenServiceImpl @Autowired constructor(
    val logProperties: LogProperties,
    val jwtParse: ParseTokenUtilService,
) : InfoExtractFromTokenService {
    override fun checkOrGetTokenInfo(token: String): String {
        return if (logProperties.fieldNameToken.isNotEmpty()
            && token != "unknown"
        ) getInfoFromToken(token) else "unknown"
    }


    fun getInfoFromToken(token: String): String {
        val extractInfo = mutableListOf<TokenInfo>()
        try {
            logProperties.fieldNameToken.forEach {
                extractInfo.add(TokenInfo(it, jwtParse.getValueFieldFromToken(token, it)))
            }
        } catch (e: NullPointerException) {
            println(e.message)
        } finally {
            val formatInfo = formatInfo(extractInfo)
            MDC.put(Constants.TOKEN_INFO_MDC, formatInfo)
            return formatInfo
        }
    }

    private fun formatInfo(extractInfo: MutableList<TokenInfo>): String {
        val formatString = StringBuilder()
        formatString.append("{")
        extractInfo.forEach {
            MDC.put("user.${it.fieldName}", it.fieldValue)
            formatString.append("\n\t${it.fieldName} = ${it.fieldValue},")
        }
        formatString.substring(0, formatString.length - 1)
        formatString.append("\n}")

        return formatString.toString()
    }
}
