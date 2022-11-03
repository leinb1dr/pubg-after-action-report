@file:Suppress("unused")

package com.leinb1dr.pub.afteractionreport.event

import com.leinb1dr.pub.afteractionreport.seasons.CurrentSeasonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Service
class SeasonUpdater(@Autowired private val currentSeasonService: CurrentSeasonService) {

    private val sink = Sinks.many().replay().latest<Long?>()

    @PostConstruct
    fun register() {
        val test = sink.asFlux()
            .publish()
        test
            .flatMap { currentSeasonService.updateSeason() }
            .subscribe(System.out::println)
        test.connect()
    }

    @Scheduled(cron = "00 08 * * 3")
    fun scheduleFixedRateTask() {
        sink.tryEmitNext(System.currentTimeMillis())
    }

    @PreDestroy
    fun shutdown(){
        sink.tryEmitComplete()
    }
}