package com.leinb1dr.pubg.afteractionreport.seasons

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class CurrentSeason(
    @Id
    val id: ObjectId = ObjectId.get(),
    val season: String
)
