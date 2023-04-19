package my.company.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import my.company.config.LogProperties
import my.company.service.CheckUrlService
import my.company.service.RequestLogHelper
import my.company.service.ResponseLogHelper
import my.company.util.Constants
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

class LogFilter @Autowired constructor(
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