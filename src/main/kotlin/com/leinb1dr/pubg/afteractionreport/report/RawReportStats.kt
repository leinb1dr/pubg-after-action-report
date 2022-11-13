package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.core.MatchAttributes
import com.leinb1dr.pubg.afteractionreport.core.ParticipantAttributes
import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.core.SeasonStats
import com.leinb1dr.pubg.afteractionreport.match.Match
import com.leinb1dr.pubg.afteractionreport.player.DefaultPlayerMatch
import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.stats.Stats

data class RawReportStats(
    val playerMatch: PlayerMatch,
    val matchStats: Stats,
    val seasonStats: Stats
) {
    constructor(participant: PubgData, match: Match, season: Stats) : this(
        DefaultPlayerMatch(participant.id, match.matchId),
        Stats.DefaultStats(
            match.data.data!![0].attributes as MatchAttributes,
            (participant.attributes as ParticipantAttributes).stats
        ),
        season
    )
}
