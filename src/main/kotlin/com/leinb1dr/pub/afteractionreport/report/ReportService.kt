package com.leinb1dr.pub.afteractionreport.report

import com.leinb1dr.pub.afteractionreport.core.PubgData
import com.leinb1dr.pub.afteractionreport.match.MatchService
import com.leinb1dr.pub.afteractionreport.player.PlayerService
import com.leinb1dr.pub.afteractionreport.usermatch.UserMatch
import com.leinb1dr.pub.afteractionreport.usermatch.UserMatchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

@Service
class ReportService(
    @Autowired val playerService: PlayerService,
    @Autowired val userMatchRepository: UserMatchRepository,
    @Autowired val matchService: MatchService
) {

    fun getLatestReport(pubgIds: List<String>): Flux<Report> {
        val players = Flux.fromIterable(pubgIds)
            .buffer(10)
            .flatMap { playerService.findPlayersByIds(it) }
            .flatMap { Flux.fromArray(it.data!!) }

        return players.flatMap {
            val data = it.relationships!!["matches"]!!.data

            val latestMatchFromPubg: Mono<PubgData> = if (data != null) Mono.just(data[0]) else Mono.empty()

            val latestMatchFromCache: Mono<UserMatch> =
                userMatchRepository.findOneByPubgId(it.id).defaultIfEmpty(UserMatch(pubgId = it.id, latestMatchId = ""))

            Flux.zip(latestMatchFromPubg, latestMatchFromCache)
        }.flatMap {
            if (it.t1.id != it.t2.latestMatchId) return@flatMap Mono.just(it)
            return@flatMap Mono.empty<Tuple2<PubgData, UserMatch>>()
        }
            .flatMap {
                val match = it.t1
                val playerMatch = it.t2.copy(latestMatchId = match.id)
                userMatchRepository.save(playerMatch)
                    .map { match }
                    .flatMap { matchService.getMatch(it.id) }
                    .participantSearch(it.t2.pubgId)
            }
            .formatReport()
    }

}
