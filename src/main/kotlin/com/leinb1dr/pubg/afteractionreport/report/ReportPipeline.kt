package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.message.MessageProcessor
import com.leinb1dr.pubg.afteractionreport.player.PlayerProcessor
import com.leinb1dr.pubg.afteractionreport.stats.StatsProcessor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReportPipeline(
    @Autowired val playerProcessor: PlayerProcessor,
    @Autowired val statsProcessor: StatsProcessor,
    @Autowired val reportProcessor: ReportProcessor,
    @Autowired val messageProcessor: MessageProcessor
) {

    private val logger = LoggerFactory.getLogger(ReportPipeline::class.java)

    //todo update the latest match for the user
    fun generateAndSend() =
        playerProcessor.findAll()
            .doOnNext { logger.debug("Looing up match results for $it") }
            .doOnComplete { logger.debug("Found all users for batch") }
            .flatMap(statsProcessor::collectStats)
            .doOnNext { logger.debug("Stats collected for $it") }
            .doOnComplete { logger.debug("All stats collected for batch") }
            .flatMap { rawReport ->
                reportProcessor.transformReport(rawReport)
                    .doOnNext { logger.debug("Report prepared for $it") }
                    .flatMap(messageProcessor::sendMessage)
                    .doOnNext { logger.debug("Message sent: $it") }
                    .flatMap { sentMessage ->
                        playerProcessor.updatePlayerMatch(rawReport.playerMatch).map { sentMessage }
                            .onErrorReturn(sentMessage)
                    }
            }

}


