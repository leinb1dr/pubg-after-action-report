package com.leinb1dr.pubg.commandgateway.gateway

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class GatewayUrlService(@Autowired @Qualifier("discord") private val webClient: WebClient) {

    fun getGatewayUrl() = webClient.get().uri("/gateway/bot").retrieve().bodyToMono<Gateway>()

}