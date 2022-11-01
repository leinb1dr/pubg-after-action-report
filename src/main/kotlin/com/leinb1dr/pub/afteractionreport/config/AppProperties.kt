package com.leinb1dr.pub.afteractionreport.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "app")
@ConstructorBinding
data class AppProperties(
    val pubgToken: String,
    val discordWebHook: String
)
