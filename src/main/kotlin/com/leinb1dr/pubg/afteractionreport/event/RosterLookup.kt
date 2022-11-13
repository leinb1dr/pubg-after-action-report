package com.leinb1dr.pubg.afteractionreport.event

import com.leinb1dr.pubg.afteractionreport.core.*
import com.leinb1dr.pubg.afteractionreport.match.MatchStorageService
import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.report.RawReportStats
import com.leinb1dr.pubg.afteractionreport.report.ReportProcessor
import com.leinb1dr.pubg.afteractionreport.report.TeamReport
import com.leinb1dr.pubg.afteractionreport.stats.Stats
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct


@Service
class RosterLookup(
    @Autowired val matchStorageService: MatchStorageService,
    @Autowired val reportProcessor: ReportProcessor,
    @Autowired val messagePublisher: MessagePublisher
) {

    private val sink = Sinks.many().replay().latest<PlayerMatch>()
    private val logger = LoggerFactory.getLogger(RosterLookup::class.java)
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
            .flatMap { playerMatch -> matchStorageService.matchExists(playerMatch.matchId).map { playerMatch } }
            .flatMap { playerMatch ->
                matchStorageService.getMatch(playerMatch.matchId)
                    .flatMap { match ->
                        Mono
                            .just(match.data.included!!.filter { it.type == "participant" }
                                .filter { (it.attributes as ParticipantAttributes).stats.playerId == playerMatch.pubgId }
                                .map { it.id }
                                .first())
                            .map { participantId ->
                                match.data.included.filter { it.type == "roster" }
                                    .find { rosterData ->
                                        rosterData.relationships!!["participants"]!!.data!!.any { it.id == participantId }
                                    }
                            }.flatMap { roster ->
                                Flux.fromArray(roster!!.relationships!!["participants"]!!.data!!)
                                    .map { participant ->
                                        match.data.included.filter { it.type == "participant" }
                                            .find { it.id == participant.id }
                                    }
                                    .flatMap {
                                        reportProcessor.transformReport(
                                            RawReportStats(
                                                object : PlayerMatch {
                                                    override val pubgId: String = it!!.id
                                                    override val matchId: String = match.matchId

                                                },
                                                object : Stats {
                                                    override val attributes: MatchAttributes? =
                                                        match.data.data!![0].attributes as MatchAttributes?
                                                    override val stats: AbstractStats =
                                                        (it!!.attributes as ParticipantAttributes).stats
                                                },
                                                object : Stats {
                                                    override val attributes: MatchAttributes? = null
                                                    override val stats: AbstractStats = SeasonStats()
                                                }
                                            ))
                                    }
                                    .collectList()
                                    .map {
                                        TeamReport(
                                            (roster.attributes as RosterAttributes).stats.rank,
                                            roster.attributes.won,
                                            it
                                        )
                                    }
                            }
                    }.map { Pair(playerMatch, it) }
            }.subscribe {
                playerMatchMap.remove(it.first)
                messagePublisher.publish(it.second)
                logger.info("Found roster for match ${it.second}")
            }
    }
}