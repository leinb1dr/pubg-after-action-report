package com.leinb1dr.pubg.commandgateway.service

import com.leinb1dr.pubg.afteractionreport.core.PlayerAttributes
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerDetailsService
import com.leinb1dr.pubg.afteractionreport.user.User
import com.leinb1dr.pubg.afteractionreport.user.UserService
import com.leinb1dr.pubg.commandgateway.exception.PubgAccountNotFound
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class RegistrationService(
    @Autowired val userService: UserService,
    @Autowired val playerDetailsService: PlayerDetailsService
) {

    fun unregister(discordId: String): Mono<User> = userService.deleteUserByDiscordId(discordId)

    fun register(discordId: String, discordName: String, username: String): Mono<User> =
        userService.getUserByDiscordId(discordId)
            .switchIfEmpty(playerDetailsService.findPlayersByNames(listOf(username))
                .switchIfEmpty { Mono.error(PubgAccountNotFound(username)) }
                .flatMap {
                    if (it.data == null) {
                        Mono.just(User(discordId = "", pubgId = ""))
                    } else {
                        Mono.just(
                            User(
                                discordId = discordId,
                                discordName = discordName,
                                pubgId = it.data!![0].id,
                                pubgName = (it.data!![0].attributes as PlayerAttributes).name
                            )
                        )
                            .flatMap(userService::registerUser)
                    }
                }

            )

}