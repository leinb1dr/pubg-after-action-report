package com.leinb1dr.pubg.afteractionreport.user

import com.leinb1dr.pubg.afteractionreport.core.GameMode
import com.leinb1dr.pubg.afteractionreport.core.SeasonAttributes
import com.leinb1dr.pubg.afteractionreport.core.SeasonStats
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "aarUser")
data class User(
    @Id
    val id: ObjectId = ObjectId.get(),
    val discordId: String,
    val discordName: String="",
    val pubgId: String,
    val pubgName: String="",
    val matchId: String = "",
    val seasonStats: Map<GameMode, SeasonStats> = mapOf()
)
