package com.leinb1dr.pubg.afteractionreport.match

import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MatchStorageService(@Autowired private val matchRepository: MatchRepository) {

    private val logger = LoggerFactory.getLogger(MatchStorageService::class.java)

    fun storeMatch(pubgWrapper: PubgWrapper) =
        matchRepository.save(Match(data = pubgWrapper, matchId = pubgWrapper.data!![0].id))
            .doOnError { logger.error("Failed to save match data", it) }
            .onErrorResume { Mono.empty() }


    fun matchExists(pubgMatchId: String) =
        matchRepository.existsByMatchId(pubgMatchId)

    fun getMatch(pubgMatchId: String) =
        matchRepository.findByMatchId(pubgMatchId)
            .doOnError { logger.error("Failed to find match data", it) }
            .onErrorResume { Mono.empty() }
}