package com.back.global.initData

import com.back.domain.member.member.service.MemberService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.transaction.annotation.Transactional

@Profile("!prod")
@Configuration
class NotProdInitData(
    private val memberService: MemberService,
    private val redisTemplate: StringRedisTemplate,
) {
    @Autowired
    @Lazy
    lateinit var self: NotProdInitData

    @Bean
    fun baseInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner {
            self.work1()
            self.work2()
        }
    }

    fun work1() {
        redisTemplate.execute { connection: RedisConnection ->
            connection.serverCommands().flushDb()
        }
    }

    @Transactional
    fun work2() {
        if (memberService.count() > 0L) return

        memberService.save("admin", "1234", "관리자")
    }
}