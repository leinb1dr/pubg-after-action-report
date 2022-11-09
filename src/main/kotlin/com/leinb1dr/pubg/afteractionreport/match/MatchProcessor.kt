package com.leinb1dr.pubg.afteractionreport.match

import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MatchProcessor(@Autowired val matchService: MatchService) {
    fun lookupDetails(playerMatch: PlayerMatch): Mono<PlayerMatchStats> =
        matchService.getMatchDetailsForPlayer(playerMatch)


}
