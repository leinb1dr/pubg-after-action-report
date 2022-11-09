package com.leinb1dr.pubg.afteractionreport.stats

import com.leinb1dr.pubg.afteractionreport.match.MatchDetailsService
import com.leinb1dr.pubg.afteractionreport.match.ReportStats
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
    @Autowired val seasonsService:SeasonService
) {
    fun collectStats(playerMatch: PlayerMatch): Mono<ReportStats> {
        val matchStatsMono = matchDetailsService.getMatchDetailsForPlayer(playerMatch)
        val seasonStatsMono = seasonsService.getCurrentSeason().flatMap { playerSeasonService.getPlayerSeasonStats(playerMatch.pubgId, it.id) }

        return Mono.zip(matchStatsMono, seasonStatsMono) {o1,o2->ReportStats(o1,o2)}
    }


}
