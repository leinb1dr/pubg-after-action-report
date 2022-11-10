package com.leinb1dr.pubg.afteractionreport.player

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

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
                    .filter { playerMatch ->
                        it[playerMatch.pubgId] != playerMatch.matchId
                    }
            }
}
