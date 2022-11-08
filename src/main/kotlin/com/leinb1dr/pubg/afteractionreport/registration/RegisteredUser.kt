package com.leinb1dr.pubg.afteractionreport.registration

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "RegisteredUser")
data class RegisteredUser(
    @Id
    val id: ObjectId = ObjectId.get(),
    val discordId: String,
    val pubgId: String)
