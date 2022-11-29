package com.leinb1dr.pubg.commandgateway.gateway.handler

import com.leinb1dr.pubg.commandgateway.gateway.events.DiscordEvent
import reactor.core.publisher.Flux
import kotlin.reflect.KClass

interface EventHandler<T:DiscordEvent> {
    fun handle(it:T): Flux<DiscordEvent>

    fun typeHandler(): KClass<T>
}