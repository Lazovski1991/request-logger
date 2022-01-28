package my.company.filter

import my.company.config.LogProperties
import my.company.service.CheckUrlService
import my.company.service.RequestLogHelper
import my.company.service.ResponseLogHelper
import my.company.util.Constants
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LogFilter constructor(
    private val logProperties: LogProperties,
    private val requestLogHelper: RequestLogHelper,
    private val responseLogHelper: ResponseLogHelper,
    private val checkUrlService: CheckUrlService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response)
        } else {
            if (logProperties.urlExclude.isNotEmpty() && checkUrlService.checkUrl(request.requestURI.toString())) {
                filterChain.doFilter(request, response)
            } else {
                request.setAttribute(Constants.TIME_START_REQUEST, System.currentTimeMillis())

                val wrappedResponse = ContentCachingResponseWrapper(response)
                val wrappedRequest = ContentCachingRequestWrapper(request)

                try {
                    filterChain.doFilter(wrappedRequest, wrappedResponse)
                } finally {
                    requestLogHelper.logRequest(wrappedRequest)
                    //request логирование выключаем в другом месте, так как если мы хотим отдельно логировать ответ,
                    // нужно чтоб сработала чать логики запроса
                    if (logProperties.enableLogResponse) responseLogHelper.logResponse(wrappedRequest, wrappedResponse)
                    wrappedResponse.copyBodyToResponse()
                    MDC.clear()
                }
            }
        }
    }
}