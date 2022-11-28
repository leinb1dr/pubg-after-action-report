package com.leinb1dr.pubg.afteractionreport.seasons

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface CurrentSeasonRepository: ReactiveMongoRepository<CurrentSeason, ObjectId> {
    fun existsBySeason(season: String): Mono<Boolean>
    fun findByCurrent(boolean: Boolean): Mono<CurrentSeason>

}