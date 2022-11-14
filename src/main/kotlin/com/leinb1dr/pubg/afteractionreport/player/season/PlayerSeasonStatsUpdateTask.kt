@file:Suppress("unused")

package com.leinb1dr.pubg.afteractionreport.player.season

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
class PlayerSeasonStatsUpdateTask(@Autowired private val processor: PlayerSeasonStatsUpdateProcessor) {

    private val sink = Sinks.many().replay().latest<Long?>()
    private val logger = LoggerFactory.getLogger(PlayerSeasonStatsUpdateTask::class.java)

    @PostConstruct
    fun register() {
        val test = sink.asFlux().publish()
        test
            .doOnNext { logger.info("Polling for changes at $it") }
            .flatMap { processor.process() }
            .subscribe{logger.info("Processed season stats $it")}
        test.connect()
    }

    @Scheduled(fixedRate = 6, timeUnit = TimeUnit.HOURS)
    fun scheduleFixedRateTask() {
        sink.tryEmitNext(System.currentTimeMillis())
    }

    @PreDestroy
    fun shutdown() {
        sink.tryEmitComplete()
    }
}