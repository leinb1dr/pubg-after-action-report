package com.leinb1dr.pubg.commandgateway.gateway.events

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

@JsonDeserialize(using = DiscordEventDeserializer::class)
abstract class DiscordEvent(
    open val op: OpCode,
    open val d: Any?,
    open val s: Int?,
    open val t: String?
)

class DiscordEventDeserializer : StdDeserializer<DiscordEvent>(DiscordEvent::class.java) {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): DiscordEvent {
        val tree = p!!.codec.readTree<JsonNode>(p)
        return when (tree.get("op").asInt()) {
            0 -> when (tree.get("t").asText()) {
                "INTERACTION_CREATE" -> p.codec.treeToValue(tree, Interaction::class.java)
                else -> p.codec.treeToValue(tree, Dispatch::class.java)
            }
            10 -> p.codec.treeToValue(tree, Hello::class.java)
            11 -> p.codec.treeToValue(tree, HeartBeatAck::class.java)
            else -> p.codec.treeToValue(tree, DebugMessage::class.java)
        }

    }


}
