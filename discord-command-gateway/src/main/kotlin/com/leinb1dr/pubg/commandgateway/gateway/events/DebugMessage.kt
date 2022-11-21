package com.leinb1dr.pubg.commandgateway.gateway.events

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize
data class DebugMessage(
    override val op: OpCode,
    override val d: Any?,
    override val s: Int? = null,
    override val t: String? = null
) :
    DiscordEvent(op, d, s, t)