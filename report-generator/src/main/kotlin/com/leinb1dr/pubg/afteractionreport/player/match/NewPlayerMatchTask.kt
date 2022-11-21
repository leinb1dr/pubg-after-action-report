@file:Suppress("unused")

package com.leinb1dr.pubg.afteractionreport.player.match

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
class NewPlayerMatchTask(@Autowired private val pipeline: NewPlayerMatchPipeline) {

    private val sink = Sinks.many().replay().latest<Long?>()
    private val logger = LoggerFactory.getLogger(NewPlayerMatchTask::class.java)

    @PostConstruct
    fun register() {
        val test = sink.asFlux().publish()
        test
            .doOnNext { logger.info("Polling for changes at $it") }
            .flatMap { pipeline.generateAndSend() }
            .subscribe{logger.info("Message sent $it")}
        test.connect()
    }

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    fun scheduleFixedRateTask() {
        sink.tryEmitNext(System.currentTimeMillis())
    }

    @PreDestroy
    fun shutdown() {
        sink.tryEmitComplete()
    }
}