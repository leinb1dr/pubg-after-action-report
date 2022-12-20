package com.leinb1dr.pubg.afteractionreport.user

import com.leinb1dr.pubg.afteractionreport.core.GameMode
import com.leinb1dr.pubg.afteractionreport.core.SeasonStats
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserService(@Autowired private val userRepository: UserRepository) {

    fun getAllUsers(): Flux<User> = userRepository.findAll()

    fun getUserByDiscordId(discordId: String): Mono<User> = userRepository.findByDiscordId(discordId)

    fun getUserByPubgId(pubgId: String): Mono<User> = userRepository.findByPubgId(pubgId)

    fun updateUserMatch(pubgId: String, matchId: String): Mono<Long> =
        userRepository.findAndSetMatchIdByPubgId(pubgId, matchId)

    fun updateUserSeasonStats(pubgId: String, seasonStats: Map<GameMode, SeasonStats>) =
        userRepository.findAndSetSeasonStatsByPubgId(pubgId, seasonStats)

    fun registerUser(user: User) = userRepository.save(user)
    fun deleteUserByDiscordId(discordId: String): Mono<Void> = userRepository.deleteByDiscordId(discordId)

}