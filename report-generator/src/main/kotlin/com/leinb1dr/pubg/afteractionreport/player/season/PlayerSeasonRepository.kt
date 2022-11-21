package com.leinb1dr.pubg.afteractionreport.player.season

import com.leinb1dr.pubg.afteractionreport.core.GameMode
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface PlayerSeasonRepository : ReactiveMongoRepository<PlayerSeason, ObjectId> {
    fun findByPubgIdAndGameMode(pubgId: String, gameMode: GameMode): Mono<PlayerSeason>
}