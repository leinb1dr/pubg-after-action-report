package com.leinb1dr.pubg.afteractionreport.message

import com.leinb1dr.pubg.afteractionreport.report.Report
import com.leinb1dr.pubg.afteractionreport.report.TeamReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MessageProcessor(@Autowired val messageService: MessageService) {

    fun sendMessage(report: Report): Mono<DiscordMessage> = Flux.just(report)
        .reportToMessageEmbedTransformer()
        .collectList()
        .mapToDiscordMessage()
        .flatMap { messageService.postMessage(it) }

    fun sendMessage(report: TeamReport): Mono<DiscordMessage> = Mono.just(report)
        .flatMapMany { Flux.fromIterable(report.reports) }
        .reportToMessageEmbedTransformer()
        .collectList()
        .mapToDiscordMessage()
        .flatMap { messageService.postMessage(it) }


}



