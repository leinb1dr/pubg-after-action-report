package com.leinb1dr.pubg.afteractionreport.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(AppProperties::class)
class AppConfiguration {

    @Bean
    @Qualifier("pubgClient")
    fun pubgClient(builder: WebClient.Builder, appProperties: AppProperties): WebClient {
        return builder.clone().defaultHeader("Accept", "application/vnd.api+json")
            .defaultHeader("Authorization", appProperties.pubgToken)
            .baseUrl("https://api.pubg.com/shards/steam")
            .build()
    }

    @Bean
    @Qualifier("discordClient")
    fun discordClient(builder: WebClient.Builder, appProperties: AppProperties): WebClient {
        return builder.clone()
            .baseUrl(appProperties.discordWebHook)
            .build()
    }
}