package com.leinb1dr.pubg.afteractionreport.match

import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.stats.Stats

data class RawReportStats(
    val playerMatch: PlayerMatch,
    val matchStats: Stats,
    val seasonStats: Stats
)
