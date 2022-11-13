package com.leinb1dr.pubg.afteractionreport.event

import com.google.common.cache.CacheBuilder
import com.leinb1dr.pubg.afteractionreport.message.MessageProcessor
import com.leinb1dr.pubg.afteractionreport.report.TeamReport
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

@Service
class MessagePublisher(
    @Autowired val messageProcessor: MessageProcessor
) {

    private val sink = Sinks.many().replay().latest<TeamReport>()
    private val logger = LoggerFactory.getLogger(MessagePublisher::class.java)

    @PostConstruct
    fun register() {
        val test = sink.asFlux()
            .publish()
        test
            .flatMap(messageProcessor::sendMessage)
            .subscribe { logger.info("Saved Match to database $it") }
        test.connect()
    }

    fun publish(teamReport: TeamReport) {
        sink.tryEmitNext(teamReport)

    }
}