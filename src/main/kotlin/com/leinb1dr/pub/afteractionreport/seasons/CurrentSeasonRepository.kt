package com.leinb1dr.pub.afteractionreport.seasons

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface CurrentSeasonRepository: ReactiveMongoRepository<CurrentSeason, ObjectId>
