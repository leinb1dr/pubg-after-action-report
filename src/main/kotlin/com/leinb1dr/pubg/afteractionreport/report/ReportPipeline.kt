package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.event.NewMatchCollector
import com.leinb1dr.pubg.afteractionreport.event.RosterLookup
import com.leinb1dr.pubg.afteractionreport.player.PlayerProcessor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReportPipeline(
    @Autowired val playerProcessor: PlayerProcessor,
    @Autowired val newMatchCollector: NewMatchCollector,
    @Autowired val rosterLookup: RosterLookup
) {

    private val logger = LoggerFactory.getLogger(ReportPipeline::class.java)

    fun generateAndSend() =
        playerProcessor.findAll()
            .doOnNext { newMatchCollector.lookupMatch(it.matchId) }
            .doOnNext { rosterLookup.addPlayerMatchToProcess(it) }
            .flatMap { playerMatch ->
                playerProcessor.updatePlayerMatch(playerMatch).onErrorReturn(1L).map { playerMatch }
            }
//                            .onErrorReturn(sentMessage) }
//            .doOnNext { logger.debug("Looing up match results for $it") }
//            .doOnComplete { logger.debug("Found all users for batch") }
//            .flatMap(statsProcessor::collectStats)
//            .doOnNext { logger.debug("Stats collected for $it") }
//            .doOnComplete { logger.debug("All stats collected for batch") }
//            .flatMap { rawReport ->
//                reportProcessor.transformReport(rawReport)
//                    .doOnNext { logger.debug("Report prepared for $it") }
//                    .flatMap(messageProcessor::sendMessage)
//                    .doOnNext { logger.debug("Message sent: $it") }
//                    .flatMap { sentMessage ->
//                        playerProcessor.updatePlayerMatch(rawReport.playerMatch).map { sentMessage }
//                            .onErrorReturn(sentMessage)
//                    }
//            }

}


