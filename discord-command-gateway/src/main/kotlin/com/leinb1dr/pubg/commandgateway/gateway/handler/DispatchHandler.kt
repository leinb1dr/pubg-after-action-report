package com.leinb1dr.pubg.commandgateway.gateway.handler

import com.leinb1dr.pubg.commandgateway.gateway.events.DiscordEvent
import com.leinb1dr.pubg.commandgateway.gateway.events.Dispatch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import kotlin.reflect.KClass

@Service
class DispatchHandler : EventHandler<Dispatch> {

    private val logger = LoggerFactory.getLogger(DispatchHandler::class.java)

    override fun handle(it: Dispatch): Flux<DiscordEvent> {
        logger.info("Got a dispatch: $it")
        return Flux.empty()
    }

    override fun typeHandler(): KClass<Dispatch> = Dispatch::class
}