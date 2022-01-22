package my.company.service

import org.springframework.web.util.ContentCachingRequestWrapper

interface RequestHelperService {
    fun logRequest(request: ContentCachingRequestWrapper)
}