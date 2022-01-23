package my.company.service

import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

interface ResponseLogHelper {
    fun logResponse(request: ContentCachingRequestWrapper, response: ContentCachingResponseWrapper)
}