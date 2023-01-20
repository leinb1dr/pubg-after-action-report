package com.leinb1dr.pubg.afteractionreport.match

import com.google.common.cache.CacheBuilder
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

@Service
class MatchProcessor(
    @Autowired private val matchStorageService: MatchStorageService,
    @Autowired private val matchDetailsService: MatchDetailsService
) {

    private val cache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build<String, String>()
    private val logger = LoggerFactory.getLogger(MatchProcessor::class.java)

    fun process(requestedMatchId: String): Mono<Match> =
        Mono.just(requestedMatchId).filter {
            if (cache.getIfPresent(it) == null) {
                cache.put(it, it)
                return@filter true
            }
            return@filter false
        }
            .doOnNext { logger.debug("Match not recently processed: $it") }
            .flatMap { matchId ->
                matchStorageService.matchExists(matchId)
                    .doOnNext { logger.info("Match[$matchId] found: $it") }
                    .filter { !it }.map { matchId }
            }
            .doOnNext { logger.debug("Match[$it] not stored in db") }
            .flatMap(matchDetailsService::getMatch)
            .flatMap(matchStorageService::storeMatch)

    fun lookup(playerMatch: PlayerMatch): Mono<Match> =
        matchStorageService.matchExists(playerMatch.matchId)
            .flatMap {
                when (it) {
                    true -> Mono.just(playerMatch)
                    false -> Mono.empty()
                }
            }
            .flatMap { matchStorageService.getMatch(it.matchId) }


}