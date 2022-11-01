package com.leinb1dr.pub.afteractionreport.usermatch

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class UserMatch (@Id
                 val id: ObjectId = ObjectId.get(),
                 val pubgId: String,
                 val latestMatchId: String)