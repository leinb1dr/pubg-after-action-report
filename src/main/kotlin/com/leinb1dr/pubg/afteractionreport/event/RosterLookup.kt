package com.leinb1dr.pubg.afteractionreport.event

import com.leinb1dr.pubg.afteractionreport.core.MatchAttributes
import com.leinb1dr.pubg.afteractionreport.match.MatchStorageService
import com.leinb1dr.pubg.afteractionreport.player.DefaultPlayerMatch
import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.player.PlayerSeasonService
import com.leinb1dr.pubg.afteractionreport.report.RawReportStats
import com.leinb1dr.pubg.afteractionreport.report.ReportProcessor
import com.leinb1dr.pubg.afteractionreport.report.TeamReport
import com.leinb1dr.pubg.afteractionreport.roster.RosterProcessor
import com.leinb1dr.pubg.afteractionreport.seasons.SeasonService
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
class RosterLookup(
    @Autowired val matchStorageService: MatchStorageService,
    @Autowired val reportProcessor: ReportProcessor,
    @Autowired val messagePublisher: MessagePublisher,
    @Autowired val playerSeasonService: PlayerSeasonService,
    @Autowired val seasonsService: SeasonService,
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
        val matchReady = Flux.fromIterable(playerMatchMap.keys)
            .flatMap { playerMatch -> matchStorageService.matchExists(playerMatch.matchId).map { playerMatch } }

        matchReady.flatMap { playerMatch ->
            matchStorageService.getMatch(playerMatch.matchId)
                .flatMap { match ->
                    RosterProcessor.extractRoster(match, playerMatch)
                        .flatMap { roster ->
                            RosterProcessor.extractParticipants(match, roster).flatMap { participant ->
                                seasonsService.getCurrentSeason()
                                    .flatMap { season ->
                                        playerSeasonService.getPlayerSeasonStats(
                                            playerMatch,
                                            season.id,
                                            (match.data.data!![0].attributes as MatchAttributes).gameMode
                                        )
                                    }
                                    .map { RawReportStats(participant!!, match, it) }
                            }
                                .flatMap(reportProcessor::transformReport)
                                .collectList()
                                .map { TeamReport(roster, match, it) }
                        }
                }.map { Pair(playerMatch, it) }
        }.subscribe {
            playerMatchMap.remove(it.first)
            messagePublisher.publish(it.second)
            logger.info("Found roster for match ${it.second}")
        }

    }
}