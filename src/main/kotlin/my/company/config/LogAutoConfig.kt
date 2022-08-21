package my.company.config

import my.company.filter.LogFilter
import my.company.jwtparselib.service.ParseTokenUtilService
import my.company.service.*
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
        @Autowired requestLogHelper: RequestLogHelper,
        @Autowired responseLogHelper: ResponseLogHelper,
        @Autowired checkUrlService: CheckUrlService
    ): LogFilter =
        LogFilter(logProperties, requestLogHelper, responseLogHelper, checkUrlService)

    @Bean
    fun createCheckUrlServiceImpl(@Autowired logProperties: LogProperties): CheckUrlService =
        CheckUrlServiceImpl(logProperties)

    @Bean
    fun createFormatService(): FormatService = FormatServiceImpl()

    @Bean
    fun createInfoExtractFromTokenService(
        @Autowired logProperties: LogProperties,
        @Autowired parseTokenUtilService: ParseTokenUtilService
    ): InfoExtractFromTokenServiceImpl =
        InfoExtractFromTokenServiceImpl(logProperties, parseTokenUtilService)

    @Bean
    fun createRequestLogHelper(
        @Autowired logProperties: LogProperties,
        @Autowired infoExtractFromTokenServiceImpl: InfoExtractFromTokenServiceImpl,
        @Autowired formatService: FormatService
    ): RequestLogHelper =
        RequestLogHelperImpl(logProperties, infoExtractFromTokenServiceImpl, formatService)

    @Bean
    fun createResponseLogHelper(@Autowired formatService: FormatService): ResponseLogHelper =
        ResponseLogHelperImpl(formatService)
}