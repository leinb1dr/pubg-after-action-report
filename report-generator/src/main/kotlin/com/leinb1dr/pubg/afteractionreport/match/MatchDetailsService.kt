package com.leinb1dr.pubg.afteractionreport.match

import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Mono


@Service
class MatchDetailsService(@Autowired @Qualifier("pubgClient") private val client: WebClient) {

    private val logger = LoggerFactory.getLogger(MatchDetailsService::class.java)

    fun getMatch(s: String): Mono<PubgWrapper> = client.get().uri("/matches/{id}", s).retrieve()
        .toEntity<PubgWrapper>()
        .map { it.body ?: throw Exception("Player Not Found") }
        .doOnError { t -> logger.error("Failed to get match details", t) }
        .onErrorResume { Mono.empty() }

}
