package com.leinb1dr.pubg.commandgateway.gateway.events

data class HeartBeat(override val d: Int, override val s: Int? = null, override val t: String? = null) :
    DiscordEvent(OpCode.HEARTBEAT, d, s, t)