package com.leinb1dr.pubg.afteractionreport.stats

import com.leinb1dr.pubg.afteractionreport.core.*
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatch
import java.io.InvalidClassException

interface Stats {
    val attributes: MatchAttributes?
    val stats: AbstractStats

    companion object {
        fun create(pubgWrapper: PubgWrapper, playerMatch: PlayerMatch, gameMode: GameMode): Stats =
            when (pubgWrapper.data!![0].type) {
                "match" -> StatsFromMatch(pubgWrapper, playerMatch)
                "playerSeason" -> StatsFromSeason(pubgWrapper, gameMode)
                else -> throw InvalidClassException("Unknown type to construct Stats")
            }

        fun create(stats: SeasonStats): Stats = DefaultStats(stats = stats)

    }

    data class DefaultStats(override val attributes: MatchAttributes? = null, override val stats: AbstractStats) : Stats

    private class StatsFromMatch(pubgWrapper: PubgWrapper, playerMatch: PlayerMatch) : Stats {
        override val attributes = pubgWrapper.data!![0].attributes!! as MatchAttributes
        override val stats: AbstractStats = pubgWrapper.included!!.filter { it.type == "participant" }
            .map { it.attributes as ParticipantAttributes }
            .filter { it.stats.playerId == playerMatch.pubgId }
            .map { it.stats }
            .first()

    }

    private class StatsFromSeason(pubgWrapper: PubgWrapper, gameMode: GameMode) : Stats {
        override val attributes: MatchAttributes? = null
        override val stats: AbstractStats =
            (pubgWrapper.data?.get(0)?.attributes as PlayerSeasonAttributes).gameModeStats[gameMode] ?: SeasonStats()
    }
}

