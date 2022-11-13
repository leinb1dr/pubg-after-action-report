package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.stats.Stats

data class RawReportStats(
    val playerMatch: PlayerMatch,
    val matchStats: Stats,
    val seasonStats: Stats
)
