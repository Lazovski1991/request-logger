package my.company.filter

import my.company.config.LogProperties
import my.company.service.RequestHelperService
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LogFilter constructor(
    private val logProperties: LogProperties,
    private val requestHelperService: RequestHelperService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (logProperties.urlExclude.contains(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }
        val wrappedRequest = ContentCachingRequestWrapper(request)
        filterChain.doFilter(wrappedRequest, response)
        requestHelperService.logRequest(wrappedRequest)
    }
}