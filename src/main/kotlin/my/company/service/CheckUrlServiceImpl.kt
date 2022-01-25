package my.company.service

import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class CheckUrlServiceImpl : CheckUrlService {
    override fun checkUrl(urlExclude: MutableList<String>, request: HttpServletRequest): Boolean {
        return urlExclude.contains(request.requestURI)
    }
}