package my.company.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "logging.service")
data class LogProperties (
     var urlExclude: MutableList<String> = mutableListOf(),//todo сделать чтоб и со звездочками урл можно было
     var enable: Boolean = false
)