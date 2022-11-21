package com.leinb1dr.pubg.afteractionreport.core

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.time.LocalDateTime
import java.time.OffsetDateTime

enum class GameMode(val label: String) {
    SOLO("solo"), SOLO_FPP("solo-fpp"), DUO("duo"), DUO_FPP("duo-fpp"), SQUAD("squad"), SQUAD_FPP("squad-fpp"), NONE("none");

    companion object {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun fromLabel(label: String): GameMode =
            values().firstOrNull { it.label == label } ?: NONE
    }
}

enum class PubgMap(val key: String, val label: String) {
    ERANGLE_RM("Baltic_Main", "Erangel (Remastered)"),
    PARAMO("Chimera_Main", "Paramo"),
    MIRAMAR("Desert_Main", "Miramar"),
    VIKENDI("DihorOtok_Main", "Vikendi"),
    ERANGLE("Erangel_Main", "Erangel"),
    HAVEN("Heaven_Main", "Haven"),
    DESTON("Kiki_Main", "Deston"),
    JACKAL("Range_Main", "Camp Jackal"),
    SANHOK("Savage_Main", "Sanhok"),
    KARAKIN("Summerland_Main", "Karakin"),
    TAEGO("Tiger_Main", "Taego"),
    NONE("none","none");

    companion object {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun fromKey(key: String): PubgMap =
            values().firstOrNull { it.key == key } ?: NONE
    }
}

abstract class PubgAttributes {
    abstract val shardId: String?
}

data class UnknownAttributes(override val shardId: String) : PubgAttributes()

data class PlayerAttributes(val name: String, val titleId: String, override val shardId: String) : PubgAttributes()

data class TelemetryAttributes(
    val name: String,
    val description: String,
    val createdAt: LocalDateTime,
    val URL: String,
    override val shardId: String = "none"
) : PubgAttributes()

data class SeasonAttributes(
    val isCurrentSeason: Boolean,
    val isOffseason: Boolean,
    override val shardId: String? = null
) : PubgAttributes()

data class PlayerSeasonAttributes(
    val gameModeStats: Map<GameMode, SeasonStats>,
    override val shardId: String? = null
) : PubgAttributes()

data class MatchAttributes(
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val duration: Int = 0,
    val gameMode: GameMode = GameMode.NONE,
    val mapName: PubgMap = PubgMap.NONE,
    val isCustomMatch: Boolean = false,
    val matchType: String = "none",
    val seasonState: String = "none",
    val titleId: String = "none",
    override val shardId: String = "steam"
) : PubgAttributes()

data class ParticipantAttributes(
    val stats: ParticipantStats,
    override val shardId: String
) : PubgAttributes()

data class RosterAttributes(
    val stats: RosterStats,
    val won: Boolean,
    override val shardId: String
) : PubgAttributes()

class PubgAttributeDeserializer : StdDeserializer<PubgAttributes>(PubgAttributes::class.java) {

    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): PubgAttributes {
        val tree = p!!.codec.readTree<JsonNode>(p)
        return when {
            tree.has("gameModeStats") -> p.codec.treeToValue(tree, PlayerSeasonAttributes::class.java)
            tree.has("isCurrentSeason") -> p.codec.treeToValue(tree, SeasonAttributes::class.java)
            tree.has("URL") -> p.codec.treeToValue(tree, TelemetryAttributes::class.java)
            tree.has("name") -> p.codec.treeToValue(tree, PlayerAttributes::class.java)
            tree.has("mapName") -> p.codec.treeToValue(tree, MatchAttributes::class.java)
            tree.has("stats") && tree["stats"].has("rank") -> p.codec.treeToValue(tree, RosterAttributes::class.java)
            tree.has("stats") && tree["stats"].has("assists") -> p.codec.treeToValue(
                tree,
                ParticipantAttributes::class.java
            )
            else -> p.codec.treeToValue(tree, UnknownAttributes::class.java)
        }

    }


}