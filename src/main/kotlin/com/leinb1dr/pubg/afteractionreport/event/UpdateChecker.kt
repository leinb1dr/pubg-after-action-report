@file:Suppress("unused")

package com.leinb1dr.pubg.afteractionreport.event

import com.leinb1dr.pubg.afteractionreport.report.ReportPipeline
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
class UpdateChecker(@Autowired private val engine: ReportPipeline) {

    private val sink = Sinks.many().replay().latest<Long?>()
    private val logger = LoggerFactory.getLogger(UpdateChecker::class.java)

    @PostConstruct
    fun register() {
        val test = sink.asFlux().publish()
        test.subscribe { engine.generateAndSend() }
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