@file:Suppress("unused")

package com.leinb1dr.pubg.afteractionreport.event

import com.leinb1dr.pubg.afteractionreport.report.ReportPipeline
import com.leinb1dr.pubg.afteractionreport.user.User
import com.leinb1dr.pubg.afteractionreport.user.UserRepository
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
class UpdateChecker(@Autowired private val engine: ReportPipeline, @Autowired val userRepository: UserRepository) {

    private val sink = Sinks.many().replay().latest<Long?>()
    private val logger = LoggerFactory.getLogger(UpdateChecker::class.java)

    @PostConstruct
    fun register() {
        val test = sink.asFlux().publish()
        test
            .doOnNext { logger.info("Polling for changes at $it") }
            .flatMap { engine.generateAndSend() }
            .subscribe{logger.info("Message sent $it")}
        test.connect()
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    fun scheduleFixedRateTask() {
        sink.tryEmitNext(System.currentTimeMillis())
    }

    @PreDestroy
    fun shutdown() {
        sink.tryEmitComplete()
    }
}