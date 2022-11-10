package com.leinb1dr.pubg.afteractionreport.stats

import com.leinb1dr.pubg.afteractionreport.core.MatchAttributes
import com.leinb1dr.pubg.afteractionreport.match.MatchDetailsService
import com.leinb1dr.pubg.afteractionreport.match.RawReportStats
import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.player.PlayerSeasonService
import com.leinb1dr.pubg.afteractionreport.seasons.SeasonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class StatsProcessor(
    @Autowired val matchDetailsService: MatchDetailsService,
    @Autowired val playerSeasonService: PlayerSeasonService,
    @Autowired val seasonsService: SeasonService
) {
    fun collectStats(playerMatch: PlayerMatch): Mono<RawReportStats> {
        val matchStatsMono = matchDetailsService.getMatchDetailsForPlayer(playerMatch)
        return seasonsService.getCurrentSeason().zipWith(matchStatsMono)
            .flatMap { matchAndSeason ->
                playerSeasonService.getPlayerSeasonStats(
                    playerMatch,
                    matchAndSeason.t1.id,
                    (matchAndSeason.t2.attributes as MatchAttributes).gameMode
                ).map { RawReportStats(matchAndSeason.t2, it) }
            }
    }


}
