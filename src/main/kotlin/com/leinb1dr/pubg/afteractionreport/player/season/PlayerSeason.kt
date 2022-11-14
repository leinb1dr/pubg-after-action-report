package com.leinb1dr.pubg.afteractionreport.player.season

import com.leinb1dr.pubg.afteractionreport.core.GameMode
import com.leinb1dr.pubg.afteractionreport.core.SeasonStats
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class PlayerSeason(
    @Id
    val id: ObjectId = ObjectId.get(),
    val pubgId: String = "",
    val gameMode: GameMode = GameMode.NONE,
    val stats: SeasonStats = SeasonStats()
)