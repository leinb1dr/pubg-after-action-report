@file:Suppress("unused")

package com.leinb1dr.pub.afteractionreport.event

import com.leinb1dr.pub.afteractionreport.report.ReportEngine
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
class UpdateChecker(@Autowired private val engine: ReportEngine) {

    private val sink = Sinks.many().replay().latest<Long?>()

    @PostConstruct
    fun register() {
        val test = sink.asFlux()
            .publish()
        test
            .flatMap { engine.checkForReports(it) }
            .subscribe(System.out::println)
        test.subscribe(System.out::println)
        test.connect()
    }

    @Scheduled(fixedRate = 60000)
    fun scheduleFixedRateTask() {
        sink.tryEmitNext(System.currentTimeMillis())
    }

    @PreDestroy
    fun shutdown(){
        sink.tryEmitComplete()
    }
}