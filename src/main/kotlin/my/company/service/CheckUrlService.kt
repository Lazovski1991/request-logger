package my.company.service

import javax.servlet.http.HttpServletRequest

interface CheckUrlService {
    fun checkUrl(request: HttpServletRequest): Boolean
}