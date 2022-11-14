package com.leinb1dr.pubg.afteractionreport.stats

import com.leinb1dr.pubg.afteractionreport.message.MessagePublisherTask
import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct


@Service
class TeamStatsTask(
    @Autowired val statsProcessor: StatsProcessor,
    @Autowired val messagePublisherTask: MessagePublisherTask
) {

    private val sink = Sinks.many().replay().latest<PlayerMatch>()
    private val logger = LoggerFactory.getLogger(TeamStatsTask::class.java)
    private final val playerMatchMap = ConcurrentHashMap<PlayerMatch, Boolean>()
    //    private val cache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build<String, String>()

    @PostConstruct
    fun register() {
        val test = sink.asFlux()
            .publish()
        test
            .subscribe { playerMatchMap[it] = true }
        test.connect()
    }

    fun addPlayerMatchToProcess(playerMatch: PlayerMatch) = sink.tryEmitNext(playerMatch)

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.SECONDS)
    fun processPlayerMatches() {
        Flux.fromIterable(playerMatchMap.keys)
            .flatMap(statsProcessor::collectStats)
            .subscribe {
                playerMatchMap.remove(it.first)
                it.second?.apply { messagePublisherTask.publish(it.second!!) }
                logger.info("Found roster for match ${it.second}")
            }

    }
}