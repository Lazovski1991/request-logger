package my.company.filter

import my.company.config.LogProperties
import my.company.model.LogModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class LogFilter @Autowired constructor(private val logProperties: LogProperties) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (logProperties.urlExclude.contains(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        var requestCopy = ContentCachingRequestWrapper(request)
        var responseCopy = ContentCachingResponseWrapper(response)

        logger.info(LogModel(request.method))

        filterChain.doFilter(request, response)
    }
}