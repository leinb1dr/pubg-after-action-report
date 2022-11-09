package com.leinb1dr.pubg.afteractionreport.player

import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PlayerDetailsService(@Autowired @Qualifier("pubgClient") private val client: WebClient) {

    private val logger = LoggerFactory.getLogger(PlayerDetailsService::class.java)

    fun findPlayer(s: String): Mono<PubgWrapper> {

        return client.get().uri("/players?filter[playerNames]={name}", s).retrieve().toEntity<PubgWrapper>()
            .map { it.body ?: throw Exception("Player Not Found") }
            .doOnError { t -> logger.error("Failed to find player by name", t) }
            .onErrorResume { Mono.empty() }

    }

    fun getPlayer(id: String): Mono<PubgWrapper> {
        return client.get().uri("/players/{id}", id).retrieve().toEntity<PubgWrapper>()
            .map { it.body ?: throw Exception("Player Not Found") }
            .doOnError { t -> logger.error("Failed to get player by id", t) }
            .onErrorResume { Mono.empty() }
    }

    fun getPlayerSeasonStats(pubgId: String, seasonId: String): Mono<PubgWrapper> {
        return client.get().uri("/players/{pubgId}/seasons/{seasonId}", pubgId, seasonId)
            .retrieve()
            .toEntity<PubgWrapper>()
            .map { it.body ?: throw Exception("No season stats missing: $seasonId") }
            .doOnError { t -> logger.error("Unable to get season stats for user", t) }
            .onErrorResume { Mono.empty() }
    }

    fun getLatestPlayerMatches(pubgIds: List<String>): Flux<PlayerMatch> {
        return findPlayersByIds(pubgIds).flatMapMany { Flux.fromArray(it.data!!) }
            .map(PlayerMatch::create)
    }

    fun findPlayersByIds(pubgIds: List<String>): Mono<PubgWrapper> {
        return client.get().uri("/players?filter[playerIds]={name}", pubgIds.joinToString(","))
            .retrieve()
            .toEntity<PubgWrapper>()
            .map { it.body ?: throw Exception("Player Not Found") }
            .doOnError { t -> logger.error("Failed to find player by ids", t) }
            .onErrorResume { Mono.empty() }
    }

}
