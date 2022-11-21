package com.leinb1dr.pubg.afteractionreport.stats

import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.match.Match
import com.leinb1dr.pubg.afteractionreport.report.RawReportStats
import com.leinb1dr.pubg.afteractionreport.report.Report

data class StatsProcessorContext(
    var match: Match? = null,
    var roster: PubgData? = null,
    var participant: PubgData? = null,
    var rawReportStats: RawReportStats? = null,
    var report: Report?=null
)