package com.leinb1dr.pubg.afteractionreport.player

import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.user.User
import java.io.InvalidClassException

interface PlayerMatch {
    val pubgId: String
    val latestMatchId: String

    companion object Factory {
        fun create(source: Any) =
            when (source) {
                is PubgData -> PlayerMatchFromPubg(source)
                is User -> PlayerMatchFromUser(source)
                else -> throw InvalidClassException("Unknown type to construct PlayerMatch")
            }
    }
}

private class PlayerMatchFromPubg(pubgData: PubgData) : PlayerMatch {
    override val pubgId: String = pubgData.id
    override val latestMatchId: String = pubgData.relationships?.get("matches")?.data?.get(0)?.id ?: ""
}

private class PlayerMatchFromUser(user: User) : PlayerMatch {
    override val pubgId: String = user.pubgId
    override val latestMatchId: String = user.latestMatchId
}