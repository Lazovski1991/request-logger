package my.company.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "logging.service")
data class LogProperties (
     var urlExclude: MutableList<String> = mutableListOf(),//todo сделать чтоб и со звездочками урл можно было
     var enable: Boolean = false,
     var tokenHeaderName:String = "AuthorizationToken",
     var filePartType: List<String> = listOf("image/jpeg") //список типов файлов имена которых логируем

//todo отдельно выключать логи на запросы, ответы, ошибки, длину стектрейса,
)