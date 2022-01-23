package my.company.filter

import my.company.config.LogProperties
import my.company.service.RequestLogHelper
import my.company.service.ResponseLogHelper
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
    private val responseLogHelper: ResponseLogHelper
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response)
        } else {
            if (logProperties.urlExclude.contains(request.requestURI)) {
                filterChain.doFilter(request, response)
                return
            }
            val wrappedResponse = ContentCachingResponseWrapper(response)
            val wrappedRequest = ContentCachingRequestWrapper(request)

            try {
                filterChain.doFilter(wrappedRequest, wrappedResponse)
            } finally {
                requestLogHelper.logRequest(wrappedRequest)
                responseLogHelper.logResponse(wrappedRequest, wrappedResponse)

                wrappedResponse.copyBodyToResponse()
                MDC.clear()
            }
        }
    }
}