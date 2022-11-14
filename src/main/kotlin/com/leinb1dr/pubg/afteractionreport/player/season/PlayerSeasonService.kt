package com.leinb1dr.pubg.afteractionreport.player.season

import com.leinb1dr.pubg.afteractionreport.core.GameMode
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerDetailsService
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.stats.Stats
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Mono

@Service
class PlayerSeasonService(@Autowired @Qualifier("pubgClient") private val client: WebClient) {

    private val logger = LoggerFactory.getLogger(PlayerDetailsService::class.java)

    fun getAllPlayerSeasonStats(pubgId: String, seasonId: String): Mono<PubgWrapper> =
        client.get().uri("/players/{pubgId}/seasons/{seasonId}", pubgId, seasonId)
            .retrieve()
            .toEntity<PubgWrapper>()
            .map { it.body ?: throw Exception("No season stats missing: $seasonId") }
            .doOnError { t -> logger.error("Unable to get season stats for user", t) }
            .onErrorResume { Mono.empty() }

    fun getPlayerSeasonStats(playerMatch: PlayerMatch, seasonId: String, gameMode: GameMode): Mono<Stats> {
        return getAllPlayerSeasonStats(playerMatch.pubgId, seasonId)
            .map { Stats.create(it, playerMatch, gameMode) }
    }

}
