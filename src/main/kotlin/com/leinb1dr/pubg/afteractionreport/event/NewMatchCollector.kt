package com.leinb1dr.pubg.afteractionreport.event

import com.google.common.cache.CacheBuilder
import com.leinb1dr.pubg.afteractionreport.match.MatchDetailsService
import com.leinb1dr.pubg.afteractionreport.match.MatchProcessor
import com.leinb1dr.pubg.afteractionreport.match.MatchStorageService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Service
class NewMatchCollector(
    @Autowired private val matchProcessor: MatchProcessor
) {

    private val sink = Sinks.many().replay().latest<String>()
    private val logger = LoggerFactory.getLogger(NewMatchCollector::class.java)
    private val cache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build<String, String>()

    @PostConstruct
    fun register() {
        val test = sink.asFlux()
            .publish()
        test
            .flatMap(matchProcessor::process)
            .subscribe { logger.info("Saved Match to database $it") }
        test.connect()
    }

    fun lookupMatch(matchId: String) {
        sink.tryEmitNext(matchId)

    }
}