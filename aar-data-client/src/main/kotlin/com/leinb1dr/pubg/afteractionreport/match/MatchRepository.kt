package com.leinb1dr.pubg.afteractionreport.match

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface MatchRepository: ReactiveMongoRepository<Match, ObjectId> {

    fun existsByMatchId(pubgMatchId: String): Mono<Boolean>

    fun findByMatchId(pubgMatchId: String): Mono<Match>
}