package my.company.config

import my.company.filter.LogFilter
import my.company.service.RequestLogHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(value = ["logging.service.enable"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(value = [LogProperties::class])
@ComponentScan("my.company")
class LogAutoConfig {
    @ConditionalOnMissingBean
    @Bean
    fun createLogFilter(
        @Autowired logProperties: LogProperties,
        @Autowired requestLogHelper: RequestLogHelper
    ): LogFilter =
        LogFilter(logProperties, requestLogHelper)
}