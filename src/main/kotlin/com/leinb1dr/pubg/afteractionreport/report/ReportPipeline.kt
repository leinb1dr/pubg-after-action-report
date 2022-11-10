package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.message.MessageProcessor
import com.leinb1dr.pubg.afteractionreport.player.PlayerProcessor
import com.leinb1dr.pubg.afteractionreport.stats.StatsProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReportPipeline(
    @Autowired val playerProcessor: PlayerProcessor,
    @Autowired val statsProcessor: StatsProcessor,
    @Autowired val reportProcessor: ReportProcessor,
    @Autowired val messageProcessor: MessageProcessor
) {

    fun generateAndSend() =
        playerProcessor.findAll()
            .flatMap(statsProcessor::collectStats)
            .flatMap(reportProcessor::transformReport)
            .flatMap(messageProcessor::sendMessage)

}


