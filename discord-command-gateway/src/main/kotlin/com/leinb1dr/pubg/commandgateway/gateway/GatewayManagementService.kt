package com.leinb1dr.pubg.commandgateway.gateway

import com.fasterxml.jackson.databind.ObjectMapper
import com.leinb1dr.pubg.commandgateway.gateway.events.DiscordEvent
import com.leinb1dr.pubg.commandgateway.gateway.state.WebsocketState
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.net.URI
import java.time.Duration
import javax.annotation.PostConstruct

@Service
class GatewayManagementService(
    @Autowired private val gatewayUrlService: GatewayUrlService,
    @Autowired private val webSocketClient: WebSocketClient,
    @Autowired @Qualifier("websocket") private val sink: Sinks.Many<DiscordEvent>,
    @Autowired private val websocketState: WebsocketState,
    @Autowired private val router: EventRouter,
    @Autowired private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(GatewayManagementService::class.java)

    @PostConstruct
    fun setup() {
        val externalEvents = sink.asFlux()
            .publish()
            .autoConnect()

        gatewayUrlService.getGatewayUrl()
            .flatMap { gatewayResponse ->
                websocketState.shards.set(gatewayResponse.shards)
                webSocketClient.execute(URI.create(gatewayResponse.url + "?v=10&encoding=json")) { session ->
                    session.send(session.receive()
                        .doOnNext { logger.info("New websocket message $it") }
                        .map {
                            objectMapper.readValue(it.payloadAsText, DiscordEvent::class.java)
                        }
                        .flatMap(router::route)
                        .mergeWith(externalEvents)
                        .map { session.textMessage(objectMapper.writeValueAsString(it)) })
                }
            }.subscribe()
    }
}