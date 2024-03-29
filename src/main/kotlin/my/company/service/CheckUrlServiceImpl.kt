package my.company.service

import jakarta.annotation.PostConstruct
import my.company.config.LogProperties
import org.springframework.beans.factory.annotation.Autowired

class CheckUrlServiceImpl @Autowired constructor(
    private val logProperties: LogProperties
) : CheckUrlService {
    lateinit var urlIncludeNotStar: MutableList<String>
    lateinit var urlIncludeSingleStar: MutableList<String>
    lateinit var urlIncludeDoubleStar: MutableList<String>

    override fun checkUrl(request: String): Boolean {

        if (urlIncludeDoubleStar.isNotEmpty()) {
            if (checkDoubleStar(request)) return true
        } else if (urlIncludeSingleStar.isNotEmpty()) {
            if (checkSingleStar(request)) return true
        } else if (urlIncludeNotStar.isNotEmpty()) {
            return urlIncludeNotStar.contains(request)
        }
        return false
    }

    private fun checkSingleStar(requestURL: String): Boolean {
        if (requestURL.split("/").size == 2) {
            val newRequestURL = "$requestURL/"
            return urlIncludeSingleStar.any { newRequestURL.startsWith(it) && newRequestURL.contains(it) }
        }
        if (urlIncludeSingleStar.any { requestURL.startsWith(it) && requestURL.contains(it) }) {
            return requestURL.split("/").size <= 3
        }
        return false
    }

    private fun checkDoubleStar(requestURL: String): Boolean {
        if (requestURL.split("/").size == 2) {
            val newRequestURL = "$requestURL/"
            return urlIncludeDoubleStar.any { newRequestURL.startsWith(it) && newRequestURL.contains(it) }
        }
        return urlIncludeDoubleStar.any { requestURL.startsWith(it) && requestURL.contains(it) }
    }

    @PostConstruct
    private fun sortExcludeUrl() {
        urlIncludeNotStar = mutableListOf()
        urlIncludeSingleStar = mutableListOf()
        urlIncludeDoubleStar = mutableListOf()

        logProperties.urlExclude.forEach {
            if (it.contains("**")) {
                urlIncludeDoubleStar.add(it.substring(0, it.length - 2))
            } else if (it.contains("*")) {
                urlIncludeSingleStar.add(it.substring(0, it.length - 1))
            } else {
                urlIncludeNotStar.add(it)
            }
        }
    }
}