package my.company.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "logging.service")
data class LogProperties(
    var urlExclude: MutableList<String> = mutableListOf(),//todo сделать чтоб и со звездочками урл можно было
    var enable: Boolean = false,
    var tokenHeaderName: String = "AuthorizationToken",
    var filePartType: List<String> = listOf("image/jpeg"), //список типов файлов имена которых логируем
    var enableLogStacktrace: Boolean = true,
    var lengthStacktrace:Int = 10000
//todo отдельно выключать логи на запросы, ответы, юзер инфо, потом науиться какие поля юзер инфо доставать, имя приложения
)