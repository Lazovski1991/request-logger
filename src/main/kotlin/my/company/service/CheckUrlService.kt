package my.company.service

import javax.servlet.http.HttpServletRequest

interface CheckUrlService {
    fun checkUrl(urlExclude: MutableList<String>, request: HttpServletRequest): Boolean
}