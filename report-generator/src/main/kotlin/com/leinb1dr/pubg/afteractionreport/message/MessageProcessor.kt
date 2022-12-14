package com.leinb1dr.pubg.afteractionreport.message

import com.leinb1dr.pubg.afteractionreport.report.TeamReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MessageProcessor(@Autowired private val messageService: MessageService) {

    fun sendMessage(report: TeamReport): Mono<DiscordMessage> = Mono.just(report)
        .filter { it.place <= 10 }
        .flatMap { teamReport ->
            Flux.fromIterable(report.reports)
                .reportToMessageFieldTransformer()
                .collectList()
                .mapToDiscordMessage(teamReport)
        }
        .flatMap { messageService.postMessage(it) }


}



