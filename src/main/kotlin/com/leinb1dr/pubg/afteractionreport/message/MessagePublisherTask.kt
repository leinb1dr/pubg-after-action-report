package com.leinb1dr.pubg.afteractionreport.message

import com.leinb1dr.pubg.afteractionreport.report.TeamReport
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Sinks
import javax.annotation.PostConstruct

@Service
class MessagePublisherTask(
    @Autowired val messageProcessor: MessageProcessor
) {

    private val sink = Sinks.many().replay().latest<TeamReport>()
    private val logger = LoggerFactory.getLogger(MessagePublisherTask::class.java)

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