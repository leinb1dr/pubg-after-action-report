package com.leinb1dr.pubg.afteractionreport.message

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class MessageService(
    @Autowired @Qualifier("discordClient") val client: WebClient,
) {
    private val logger = LoggerFactory.getLogger(MessageService::class.java)

    fun postMessage(message: DiscordMessage): Mono<DiscordMessage> {

        return client.post().bodyValue(message).exchangeToMono {
            Mono.just(message)
                .doOnError { t -> logger.error("Failed to send message to discord", t) }
                .onErrorResume { Mono.empty() }
        }
    }
}