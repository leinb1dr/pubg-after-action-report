package com.leinb1dr.pubg.commandgateway.config

import com.leinb1dr.pubg.commandgateway.gateway.events.DiscordEvent
import com.leinb1dr.pubg.commandgateway.gateway.handler.EventHandler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Sinks
import kotlin.reflect.KClass

@Configuration
class CommandGatewayConfiguration {

    @Bean("handlers")
    fun handlerMap(applicationContext: ApplicationContext, handlers: Array<EventHandler<*>>): Map<KClass<DiscordEvent>, EventHandler<DiscordEvent>> {
        return handlers.map { it as EventHandler<DiscordEvent> }.associateBy { it.typeHandler() }
//        return mapOf()
    }

    @Bean("websocket")
    @Qualifier("websocket")
    fun websocketSendSink(): Sinks.Many<DiscordEvent> {
        return Sinks.many().replay().latest()
    }


    @Bean("heartbeat")
    @Qualifier("heartbeat")
    fun heartbeatSink(): Sinks.Many<Long> {
        return Sinks.many().replay().latest()
    }
}