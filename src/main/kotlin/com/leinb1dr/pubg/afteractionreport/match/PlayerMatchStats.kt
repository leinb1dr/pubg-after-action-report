package com.leinb1dr.pubg.afteractionreport.match

import com.leinb1dr.pubg.afteractionreport.core.MatchAttributes
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper

interface PlayerMatchStats {
    val attributes: MatchAttributes

    companion object {
        fun create(pubgWrapper: PubgWrapper): PlayerMatchStats {
            return PlayerMatchStatsFromPubg(pubgWrapper)
        }
    }

    private class PlayerMatchStatsFromPubg(pubgWrapper: PubgWrapper):PlayerMatchStats{
        override val attributes = pubgWrapper.data!![0].attributes!! as MatchAttributes
    }
}

