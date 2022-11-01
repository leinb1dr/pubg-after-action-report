package com.leinb1dr.pub.afteractionreport.core

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.time.LocalDateTime
import java.time.OffsetDateTime

abstract class PubgAttributes {
    abstract val shardId: String
}

data class UnknownAttributes(override val shardId: String) : PubgAttributes()

data class PlayerAttributes(val name: String, val titleId: String, override val shardId: String) : PubgAttributes()

data class TelemetryAttributes(val name: String, val description: String, val createdAt: LocalDateTime, val URL: String, override val shardId: String="none") : PubgAttributes()

data class MatchAttributes(
    val createdAt: OffsetDateTime,
    val duration: Int,
    val gameMode: String,
    val mapName: String,
    val isCustomMatch: Boolean,
    val matchType: String,
    val seasonState: String,
    val titleId: String,
    override val shardId: String) : PubgAttributes()

data class ParticipantAttributes(
    val stats: ParticipantStats,
    override val shardId: String
): PubgAttributes()

class PubgAttributeDeserializer : StdDeserializer<PubgAttributes>(PubgAttributes::class.java) {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): PubgAttributes {
        val tree = p!!.codec.readTree<JsonNode>(p)
        if(tree.has("URL")) return p.codec.treeToValue(tree, TelemetryAttributes::class.java)
        if(tree.has("name")) return p.codec.treeToValue(tree, PlayerAttributes::class.java)
        if(tree.has("mapName")) return p.codec.treeToValue(tree, MatchAttributes::class.java)
        if(tree.has("stats")) return p.codec.treeToValue(tree, ParticipantAttributes::class.java)
        return p.codec.treeToValue(tree, UnknownAttributes::class.java)

    }


}