package com.leinb1dr.pubg.commandgateway.gateway.events

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

@JsonDeserialize
data class Dispatch(
    override val op: OpCode,
    @JsonDeserialize(using = DispatchDeserializer::class)
    override val d: Any?,
    override val s: Int?,
    override val t: String?
) :
    DiscordEvent(op, d, s, t) {
    class DispatchDeserializer : StdDeserializer<Any>(Any::class.java) {

        override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Any {
            val tree = p!!.codec.readTree<JsonNode>(p)
            return when (tree.get("t")?.asText()?:"") {
                "INTERACTION_CREATE" -> p.codec.treeToValue(tree, InteractionPayload::class.java)
                else -> p.codec.treeToValue(tree, Map::class.java)
            }

        }
    }
}
