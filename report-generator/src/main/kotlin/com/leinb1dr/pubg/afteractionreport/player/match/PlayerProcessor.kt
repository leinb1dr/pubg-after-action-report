package com.leinb1dr.pubg.afteractionreport.player.match

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PlayerProcessor(
    @Autowired private val playerMatchService: PlayerMatchService,
    @Autowired private val playerDetailsService: PlayerDetailsService
) {
    fun findAll(): Flux<PlayerMatch> =
        playerMatchService.getProcessedPlayerMatches()
            .collectMap({ it.pubgId }, { it.matchId })
            .flatMapMany {
                playerDetailsService.getLatestPlayerMatches(listOf(elements = it.keys.toTypedArray()))
                    .map(PlayerMatch.Factory::create)
                    .filter { playerMatch ->
                        it[playerMatch.pubgId] != playerMatch.matchId
                    }
            }

    fun updatePlayerMatch(playerMatch: PlayerMatch): Mono<Long> =
        playerMatchService.updatePlayerMatch(playerMatch.pubgId, playerMatch.matchId)
}
