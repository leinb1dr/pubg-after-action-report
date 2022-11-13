package com.leinb1dr.pubg.afteractionreport.report

data class TeamReport(
    val place: Int,
    val won: Boolean,
    val reports:MutableList<Report>
)
