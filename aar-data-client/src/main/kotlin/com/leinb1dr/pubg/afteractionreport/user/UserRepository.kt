package com.leinb1dr.pubg.afteractionreport.user

import com.leinb1dr.pubg.afteractionreport.core.GameMode
import com.leinb1dr.pubg.afteractionreport.core.SeasonStats
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.Update
import reactor.core.publisher.Mono

interface UserRepository : ReactiveMongoRepository<User, String> {

    @Update("{ '\$set' : { 'matchId' : ?1 } }")
    fun findAndSetMatchIdByPubgId(pubgId: String, matchId: String): Mono<Long>

    @Update("{ '\$set' : { 'seasonStats' : ?1 } }")
    fun findAndSetSeasonStatsByPubgId(pubgId: String, seasonStats: Map<GameMode, SeasonStats>): Mono<Long>

    fun findByPubgId(pubgId: String): Mono<User>

    fun findByDiscordId(discordId: String): Mono<User>

}