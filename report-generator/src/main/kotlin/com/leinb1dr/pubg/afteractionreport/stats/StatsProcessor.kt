package com.leinb1dr.pubg.afteractionreport.stats

import com.google.common.cache.CacheBuilder
import com.leinb1dr.pubg.afteractionreport.core.MatchAttributes
import com.leinb1dr.pubg.afteractionreport.core.ParticipantAttributes
import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.match.Match
import com.leinb1dr.pubg.afteractionreport.match.MatchProcessor
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.player.season.PlayerSeasonStorageService
import com.leinb1dr.pubg.afteractionreport.report.RawReportStats
import com.leinb1dr.pubg.afteractionreport.report.ReportProcessor
import com.leinb1dr.pubg.afteractionreport.report.TeamReport
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

@Service
class StatsProcessor(
    @Autowired val matchProcessor: MatchProcessor,
    @Autowired val playerSeasonStorageService: PlayerSeasonStorageService,
    @Autowired val reportProcessor: ReportProcessor
) {
    private val cache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build<String, String>()


    fun collectStats(playerMatch: PlayerMatch): Mono<Pair<PlayerMatch, TeamReport?>> =
        matchProcessor.lookup(playerMatch)
            .extractRoster(playerMatch)
            .flatMap { context ->
                return@flatMap if (context.roster!!.id.isEmpty()) {
                    Mono.just(Pair(playerMatch, null))
                } else {
                    Mono.just(context).splitParticipants()
                        .gatherSeasonStatsForParticipants()
                        .flatMap(reportProcessor::transformReport)
                        .collectList()
                        .map {
                            Pair(
                                playerMatch,
                                TeamReport(it[0].roster, it[0].match, it.map { context -> context.report!! })
                            )
                        }
                }
            }


    private fun Flux<StatsProcessorContext>.gatherSeasonStatsForParticipants(): Flux<StatsProcessorContext> =
        this.flatMap { context ->
            playerSeasonStorageService.getSeasonStats(
                (context.participant!!.attributes as ParticipantAttributes).stats.playerId,
                (context.match!!.data.data!![0].attributes as MatchAttributes).gameMode
            )
                .map { Stats.create(it.stats) }
                .map { RawReportStats(context.participant!!, context.match!!, it) }
                .map {
                    context.rawReportStats = it
                    context
                }
        }

    @OptIn(InternalCoroutinesApi::class)
    private fun Mono<Match>.extractRoster(playerMatch: PlayerMatch): Mono<StatsProcessorContext> =
        this.flatMap { match ->
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
                }
                .filter {
                    synchronized(cache) {
                        if (cache.getIfPresent(it!!.id) == null) {
                            cache.put(it.id, it.id)
                            return@filter true
                        }
                        return@filter false
                    }
                }
                .doOnNext { cache.put(it!!.id, it.id) }
                .defaultIfEmpty(PubgData())
                .map {
                    val context = StatsProcessorContext()
                    context.match = match
                    context.roster = it
                    context
                }
        }
}


private fun Mono<StatsProcessorContext>.splitParticipants() = this.flatMapMany { context ->
    Flux.fromArray(context.roster!!.relationships!!["participants"]!!.data!!)
        .map { participant ->
            context.match!!.data.included!!.filter { it.type == "participant" }
                .find { it.id == participant.id }
        }.map {
            return@map context.copy(participant = it)
        }
}


