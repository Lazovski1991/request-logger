package my.company.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "logging.service")
data class LogProperties(
    var urlExclude: MutableList<String> = mutableListOf(),
    var enable: Boolean = false,
    var tokenHeaderName: String?,
    var fieldNameToken: List<String> = listOf(),
    var filePartType: List<String> = listOf("image/jpeg"), //список типов файлов имена которых логируем
    var enableLogStacktrace: Boolean = true,
    var lengthStacktrace: Int = 10000,
    var enableLogRequest: Boolean = true,
    var enableLogResponse: Boolean = true
)