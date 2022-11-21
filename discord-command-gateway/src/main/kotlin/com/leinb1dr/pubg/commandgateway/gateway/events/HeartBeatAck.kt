package com.leinb1dr.pubg.commandgateway.gateway.events

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize
data class HeartBeatAck(override val d: Int? = null, override val s: Int? = null, override val t: String? = null) :
    DiscordEvent(OpCode.HEARTBEAT_ACK, d, s, t)