package com.leinb1dr.pubg.commandgateway.gateway.events

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize
data class Hello(override val op: OpCode, override val d: HelloPayload, override val s: Int?, override val t: String?) :
    DiscordEvent(op, d, s, t)

data class HelloPayload(
    @JsonProperty("heartbeat_interval")
    val heartbeatInterval: String
)