package com.leinb1dr.pubg.afteractionreport.player.match

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

    fun getLatestPlayerMatches(pubgIds: List<String>): Flux<PlayerMatch> {
        return findPlayersByIds(pubgIds).flatMapMany { Flux.fromArray(it.data!!) }
            .map(PlayerMatch.Factory::create)
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
