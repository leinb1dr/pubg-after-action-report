package com.leinb1dr.pubg.commandgateway.gateway.events

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

@JsonDeserialize
data class Interaction(
    override val op: OpCode,
    override val d: InteractionPayload,
    override val s: Int?,
    override val t: String?
) :
    DiscordEvent(op, d, s, t)
