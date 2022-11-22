package com.leinb1dr.pubg.commandgateway.gateway

import com.fasterxml.jackson.databind.ObjectMapper
import com.leinb1dr.pubg.commandgateway.config.DiscordProperties
import com.leinb1dr.pubg.commandgateway.gateway.events.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.net.URI
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.annotation.PostConstruct

@Service
class GatewayManagementService(
    @Autowired private val gatewayUrlService: GatewayUrlService,
    @Autowired private val webSocketClient: WebSocketClient,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired val discordProperties: DiscordProperties
) {
    private val sink = Sinks.many().replay().latest<DiscordEvent>()
    private val logger = LoggerFactory.getLogger(GatewayManagementService::class.java)
    var heartBeatInterval: Int = 1000
    var shards: Int = 1

    @PostConstruct
    fun setup() {
        val externalEvents = sink.asFlux()
            .publish()
            .autoConnect()

        gatewayUrlService.getGatewayUrl()
            .flatMap { gatewayResponse ->
                shards = gatewayResponse.shards
                webSocketClient.execute(URI.create(gatewayResponse.url + "?v=10&encoding=json")) { session ->

                    val messageProcessing = session.receive()
                        .doOnNext { logger.info("New websocket message $it") }
                        .map {
                            objectMapper.readValue(it.payloadAsText, DiscordEvent::class.java)
                        }
                        .flatMap { route(it) }


                    val allEvents = Flux.merge(
                        messageProcessing,
                        externalEvents
                    ).map { session.textMessage(objectMapper.writeValueAsString(it)) }

                    session.send(allEvents)

                }
            }.subscribe()
    }

    fun route(discordEvent: DiscordEvent): Flux<DiscordEvent> = Flux.just(discordEvent)
        .doOnNext { logger.info("New discord event received: $it") }
        .flatMap {
            when (it) {
                is Hello -> {
                    heartBeatInterval = it.d.heartbeatInterval.toInt()
                    scheduleHeartbeat()
                    Flux.just(
                        Identity(
                            IdentityPayload(
                                discordProperties.token,
                                52224,
                                shards = arrayOf(0, shards),
                                ConnectionProperties(
                                    System.getProperty("os.name").lowercase(),
                                    WebSocketClient::class.qualifiedName!!
                                )
                            )
                        )
                    )
                }
                is HeartBeatAck -> {
                    scheduleHeartbeat()
                    Flux.empty()
                }
                else -> {
                    logger.info("No handler for $it")
                    Flux.empty()
                }
            }
        }

    fun scheduleHeartbeat() {
        val nextHeartbeat = (heartBeatInterval * Math.random()).toLong()
        logger.info("Next heartbeat should be sent at: ${LocalDateTime.now().plus(nextHeartbeat, ChronoUnit.MILLIS)}")
        kotlin.run {
            Mono.delay(Duration.ofMillis(nextHeartbeat))
                .doOnNext{logger.info("Heartbeat triggered")}
                .subscribe { sink.tryEmitNext(HeartBeat(1)) }
        }
    }

    fun publish(event: DiscordEvent): Mono<DiscordEvent> {
        sink.tryEmitNext(event)
        return Mono.just(event)
    }

}