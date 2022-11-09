package com.leinb1dr.pubg.afteractionreport.registration

import com.leinb1dr.pubg.afteractionreport.player.PlayerDetailsService
import com.leinb1dr.pubg.afteractionreport.user.User
import com.leinb1dr.pubg.afteractionreport.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserRegistrationService(
    @Autowired val playerDetailsService: PlayerDetailsService,
    @Autowired val userRepository: UserRepository
) {

    fun registerUser(discordId: String, pubgUserName: String): Mono<User> =
        playerDetailsService.findPlayer(pubgUserName)
            .map { it.data!![0].id }
            .map { User(discordId = discordId, pubgId = it, latestMatchId = "") }
            .flatMap { userRepository.insert(it) }


    fun getRegisteredUser(discordId: String): Mono<User> = userRepository.findById(discordId)

}
