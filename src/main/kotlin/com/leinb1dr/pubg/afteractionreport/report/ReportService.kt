package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.core.MatchAttributes
import com.leinb1dr.pubg.afteractionreport.core.PlayerSeasonAttributes
import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.core.SeasonStats
import com.leinb1dr.pubg.afteractionreport.match.MatchService
import com.leinb1dr.pubg.afteractionreport.player.PlayerService
import com.leinb1dr.pubg.afteractionreport.seasons.SeasonService
import com.leinb1dr.pubg.afteractionreport.usermatch.UserMatch
import com.leinb1dr.pubg.afteractionreport.usermatch.UserMatchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

@Service
class ReportService(
    @Autowired val playerService: PlayerService,
    @Autowired val userMatchRepository: UserMatchRepository,
    @Autowired val matchService: MatchService,
    @Autowired val seasonService: SeasonService
) {

    fun getLatestReport(pubgIds: List<String>): Flux<Report> {
        val players = Flux.fromIterable(pubgIds)
            .buffer(10)
            .flatMap { playerService.findPlayersByIds(it) }
            .flatMap { Flux.fromArray(it.data!!) }

        return players
            .flatMap {
                val data = it.relationships!!["matches"]!!.data

                val latestMatchFromPubg: Mono<PubgData> = if (data != null) Mono.just(data[0]) else Mono.empty()

                val latestMatchFromCache: Mono<UserMatch> =
                    userMatchRepository.findOneByPubgId(it.id)
                        .defaultIfEmpty(UserMatch(pubgId = it.id, latestMatchId = ""))

                Flux.zip(latestMatchFromPubg, latestMatchFromCache)
            }
            .flatMap {
                if (it.t1.id != it.t2.latestMatchId) return@flatMap Mono.just(it)
                return@flatMap Mono.empty<Tuple2<PubgData, UserMatch>>()
            }
            .flatMap { playerMatchTuple ->
                seasonService.getCurrentSeason()
                    .defaultIfEmpty(PubgData())
                    .flatMap {
                        if (it.id.isNotEmpty())
                            playerService.getPlayerSeasonStats(playerMatchTuple.t2.pubgId, it.id)
                                .map { playerSeasonStatsWrapper ->
                                    (playerSeasonStatsWrapper.data!![0].attributes as PlayerSeasonAttributes).gameModeStats[(playerMatchTuple.t1.attributes as MatchAttributes).gameMode]!!
                                }
                        else Mono.just(SeasonStats.empty())
                    }.map { Triple(playerMatchTuple.t1, playerMatchTuple.t2, it) }


            }
            .flatMap {
                val match = it.first
                val playerMatch = it.second.copy(latestMatchId = match.id)
                userMatchRepository.save(playerMatch)
                    .map { match }
                    .flatMap { pubgData -> matchService.getMatch(pubgData.id) }
                    .participantSearch(it.second.pubgId, it.third)
            }
            .filter { it.second.stats.winPlace <= 10 }
            .formatReport()
    }

}
