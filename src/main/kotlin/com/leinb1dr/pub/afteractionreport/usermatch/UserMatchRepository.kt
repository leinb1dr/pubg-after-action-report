package com.leinb1dr.pub.afteractionreport.usermatch

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface UserMatchRepository: ReactiveMongoRepository<UserMatch, String>{
    fun findOneByPubgId(pubgId:String): Mono<UserMatch>
}