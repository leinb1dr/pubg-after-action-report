package com.leinb1dr.pubg.afteractionreport.seasons

import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.core.SeasonAttributes
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class SeasonService(@Autowired @Qualifier("pubgClient") private val client: WebClient) {

    private val logger: Logger = LoggerFactory.getLogger(SeasonService::class.java)

    fun getCurrentSeason(): Mono<PubgData> {
        return client.get().uri("/seasons").retrieve().toEntity<PubgWrapper>()
            .map { it.body ?: throw Exception("No Season Data") }
            .doOnError { t -> logger.error("Failed to find player by name", t) }
            .flatMapMany { Flux.fromArray(it.data!!) }
            .filter { (it.attributes as SeasonAttributes).isCurrentSeason }
            .toMono()
            .onErrorResume { Mono.empty() }
    }

}