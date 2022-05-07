package my.company.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "logging.service")
data class LogProperties(
    var enable: Boolean = true,
    var urlExclude: MutableList<String> = mutableListOf(),
    var auth: AuthInfo = AuthInfo(),
    var filePartType: List<String> = listOf("image/jpeg", "image/png", "image/jpg"), //список типов файлов имена которых логируем
    var enableLogRequest: Boolean = true,
    var enableLogResponse: Boolean = true
)