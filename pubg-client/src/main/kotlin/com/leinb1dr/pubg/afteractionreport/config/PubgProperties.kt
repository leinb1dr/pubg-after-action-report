package com.leinb1dr.pubg.afteractionreport.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "app.pubg")
@ConstructorBinding
data class PubgProperties(
    val token: String
)