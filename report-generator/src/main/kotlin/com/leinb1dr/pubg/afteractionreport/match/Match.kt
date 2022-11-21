package com.leinb1dr.pubg.afteractionreport.match

import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
data class Match(
    @Id
    val id: ObjectId = ObjectId.get(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val data: PubgWrapper,
    val matchId: String
)