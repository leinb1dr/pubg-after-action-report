package com.leinb1dr.pubg.commandgateway.gateway

import com.leinb1dr.pubg.commandgateway.gateway.events.InteractionResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class InteractionResponseService(@Autowired @Qualifier("discord") private val webClient: WebClient) {

    fun respondToInteraction(id: String, token: String, response: InteractionResponse) =
        webClient.post()
            .uri("/interactions/{id}/{token}/callback", id, token)
            .bodyValue(response)
            .exchangeToMono { Mono.just(it.statusCode()) }

}