package com.leinb1dr.pubg.afteractionreport.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(PubgProperties::class)
@ComponentScan("com.leinb1dr.pubg.afteractionreport")
class PubgClientConfiguration {

    @Bean
    @Qualifier("pubgClient")
    fun pubgClient(builder: WebClient.Builder, appProperties: PubgProperties): WebClient {
        return builder.clone().defaultHeader("Accept", "application/vnd.api+json")
            .defaultHeader("Authorization", appProperties.token)
            .baseUrl("https://api.pubg.com/shards/steam")
            .build()
    }

}