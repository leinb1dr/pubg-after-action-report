package com.leinb1dr.pubg.afteractionreport.message

import com.leinb1dr.pubg.afteractionreport.report.Report
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MessageProcessor(@Autowired val messageService: MessageService) {

    fun sendMessage(report: Report): Mono<DiscordMessage> = Mono.just(report)
            .reportToMessageTransformer()
            .flatMap { messageService.postMessage(it) }



}