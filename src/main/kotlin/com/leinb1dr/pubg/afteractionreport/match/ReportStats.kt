package com.leinb1dr.pubg.afteractionreport.match

import com.leinb1dr.pubg.afteractionreport.stats.Stats

data class ReportStats(
    val matchStats: Stats,
    val seasonStats: Stats
)
