package com.leinb1dr.pubg.afteractionreport.stats

import com.leinb1dr.pubg.afteractionreport.core.MatchAttributes
import com.leinb1dr.pubg.afteractionreport.core.PubgAttributes
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.core.SeasonAttributes
import java.io.InvalidClassException

interface Stats {
    val attributes: MatchAttributes?

    companion object {
        fun create(pubgWrapper: PubgWrapper): Stats =
            when (pubgWrapper.data!![0].type) {
                "match" -> StatsFromMatch(pubgWrapper)
                "playerSeason" -> StatsFromSeason(pubgWrapper)
                else -> throw InvalidClassException("Unknown type to construct Stats")
            }

    }

    private class StatsFromMatch(pubgWrapper: PubgWrapper) : Stats {
        override val attributes = pubgWrapper.data!![0].attributes!! as MatchAttributes
    }

    private class StatsFromSeason(pubgWrapper: PubgWrapper) : Stats {
        override val attributes = null
    }
}

