package com.leinb1dr.pubg.commandgateway.gateway.handler

import com.leinb1dr.pubg.commandgateway.gateway.events.DiscordEvent
import com.leinb1dr.pubg.commandgateway.gateway.events.HeartBeatAck
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import kotlin.reflect.KClass

@Service
class HeartbeatAckHandler(
    @Autowired @Qualifier("heartbeat") private val sink: Sinks.Many<Long>,
) : EventHandler<HeartBeatAck> {
    override fun handle(it: HeartBeatAck): Flux<DiscordEvent> {
        sink.tryEmitNext(1)
        return Flux.empty()
    }

    override fun typeHandler(): KClass<HeartBeatAck> = HeartBeatAck::class
}