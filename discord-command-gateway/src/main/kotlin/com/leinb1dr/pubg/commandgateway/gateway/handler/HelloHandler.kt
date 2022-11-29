package com.leinb1dr.pubg.commandgateway.gateway.handler

import com.leinb1dr.pubg.commandgateway.config.DiscordProperties
import com.leinb1dr.pubg.commandgateway.gateway.events.*
import com.leinb1dr.pubg.commandgateway.gateway.state.WebsocketState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import kotlin.reflect.KClass

@Service
class HelloHandler(
    @Autowired val discordProperties: DiscordProperties,
    @Autowired @Qualifier("heartbeat") private val heartbeat: Sinks.Many<Long>,
    @Autowired val websocketState: WebsocketState
) : EventHandler<Hello> {
    override fun handle(it: Hello): Flux<DiscordEvent> {
        websocketState.heartbeatInterval.set(it.d.heartbeatInterval.toInt())
        heartbeat.tryEmitNext(1)
        return Flux.just(
            Identity(
                IdentityPayload(
                    discordProperties.token,
                    52224,
                    shards = arrayOf(0, websocketState.shards.get()),
                    ConnectionProperties(
                        System.getProperty("os.name").lowercase(),
                        WebSocketClient::class.qualifiedName!!
                    )
                )
            )
        )
    }

    override fun typeHandler(): KClass<Hello> = Hello::class
}