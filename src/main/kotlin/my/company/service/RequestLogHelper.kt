package my.company.service

import org.springframework.web.util.ContentCachingRequestWrapper

interface RequestLogHelper {
    fun logRequest(request: ContentCachingRequestWrapper)
}