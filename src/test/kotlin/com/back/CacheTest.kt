package com.back

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest
class CacheTest {

    @Autowired
    private lateinit var cacheTestService: CacheTestService

    @Test
    fun `cache Test`() {
        val key = "testKey"

        val firstValue = cacheTestService.getCachedValue(key)
        println("First call: $firstValue")

        Thread.sleep(1000) // Wait for a second to ensure the timestamp would be different if not cached

        val secondValue = cacheTestService.getCachedValue(key)
        println("Second call: $secondValue")

        assert(firstValue == secondValue) { "Expected cached value to be the same, but got different values." }
    }
}

@Service
class CacheTestService {
    @Cacheable(cacheNames = ["test"], key = "#key")
    fun getCachedValue(key: String): String {
        return "generated-at-${LocalDateTime.now()}"
    }
}