package com.leinb1dr.pubg.commandgateway.gateway

import com.leinb1dr.pubg.commandgateway.gateway.events.DiscordEvent
import com.leinb1dr.pubg.commandgateway.gateway.handler.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import kotlin.reflect.KClass

@Service
class EventRouter(@Autowired private val handlerMap: Map<KClass<DiscordEvent>, EventHandler<DiscordEvent>>) {

    private val logger = LoggerFactory.getLogger(EventRouter::class.java)

    fun route(event: DiscordEvent): Flux<DiscordEvent> =
        handlerMap[event::class]?.handle(event) ?: run {
            logger.info("Could not find router")
            Flux.empty()
        }
}