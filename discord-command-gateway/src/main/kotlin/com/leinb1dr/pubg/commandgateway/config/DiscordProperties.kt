package com.leinb1dr.pubg.commandgateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "app.discord")
@ConstructorBinding
data class DiscordProperties(
    val token: String
)
