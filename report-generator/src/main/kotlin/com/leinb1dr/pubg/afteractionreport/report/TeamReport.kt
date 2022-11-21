package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.core.MatchAttributes
import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.core.RosterAttributes
import com.leinb1dr.pubg.afteractionreport.match.Match

data class TeamReport(
    val place: Int,
    val won: Boolean,
    val matchAttributes: MatchAttributes,
    val reports:List<Report>
) {
    constructor(roster: PubgData?, match: Match?, reports: List<Report>) : this(
        (roster!!.attributes as RosterAttributes).stats.rank,
        (roster.attributes as RosterAttributes).won,
        match!!.data.data!![0].attributes as MatchAttributes,
        reports
    )
}
