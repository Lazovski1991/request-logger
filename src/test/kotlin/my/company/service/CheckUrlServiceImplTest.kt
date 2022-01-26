package my.company.service

import my.company.config.LogProperties
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired

@ExtendWith(MockitoExtension::class)
internal class CheckUrlServiceImplTest @Autowired constructor(
    @Mock val logProperties: LogProperties,
    @InjectMocks val checkUrlService: CheckUrlService
) {
    @Test
    fun checkUrl() {
    }
}