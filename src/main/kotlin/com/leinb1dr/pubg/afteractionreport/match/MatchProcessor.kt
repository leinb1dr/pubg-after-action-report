package com.leinb1dr.pubg.afteractionreport.match

import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.stats.Stats
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MatchProcessor(@Autowired val matchDetailsService: MatchDetailsService) {
    fun lookupDetails(playerMatch: PlayerMatch): Mono<Stats> =
        matchDetailsService.getMatchDetailsForPlayer(playerMatch)


}
