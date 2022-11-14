package com.leinb1dr.pubg.afteractionreport.player.match

import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.user.User
import java.io.InvalidClassException

interface PlayerMatch {
    val pubgId: String
    val matchId: String

    companion object Factory {
        fun create(source: Any) =
            when (source) {
                is PubgData -> PlayerMatchFromPubg(source)
                is User -> PlayerMatchFromUser(source)
                is DefaultPlayerMatch -> source
                else -> throw InvalidClassException("Unknown type to construct PlayerMatch")
            }
    }
}

data class DefaultPlayerMatch(
    override val pubgId: String,
    override val matchId: String
) : PlayerMatch

private data class PlayerMatchFromPubg(val pubgData: PubgData) : PlayerMatch {
    override val pubgId: String = pubgData.id
    override val matchId: String = pubgData.relationships?.get("matches")?.data?.get(0)?.id ?: ""

}

private data class PlayerMatchFromUser(val user: User) : PlayerMatch {
    override val pubgId: String = user.pubgId
    override val matchId: String = user.matchId
}