package com.leinb1dr.pubg.commandgateway.config

import com.leinb1dr.pubg.commandgateway.gateway.events.DiscordEvent
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.socket.client.StandardWebSocketClient
import org.springframework.web.reactive.socket.client.WebSocketClient
import reactor.core.publisher.Sinks

@Configuration
@EnableConfigurationProperties(DiscordProperties::class)
class WebClientConfiguration {

    @Bean
    fun webSocketClient():WebSocketClient = StandardWebSocketClient()

    @Bean
    fun heartbeatSink() {
        Sinks.many().replay().latest<DiscordEvent>()
    }

    @Bean
    @Qualifier("discord")
    fun discordClient(builder: WebClient.Builder, discordProperties: DiscordProperties): WebClient {
        return builder.clone()
            .baseUrl("https://discord.com/api")
            .defaultHeaders {
                it["Authorization"]="Bot ${discordProperties.token}"
                it["Accept"]="application/vnd.api+json"
            }
            .build()
    }

}