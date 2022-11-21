package com.leinb1dr.pubg.afteractionreport.user

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.Update
import reactor.core.publisher.Mono

interface UserRepository : ReactiveMongoRepository<User, String> {

    @Update("{ '\$set' : { 'matchId' : ?1 } }")
    fun findAndSetMatchIdByPubgId(pubgId: String, matchId: String): Mono<Long>
}