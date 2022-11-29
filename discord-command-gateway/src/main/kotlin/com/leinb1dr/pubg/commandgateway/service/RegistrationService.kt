package com.leinb1dr.pubg.commandgateway.service

import com.leinb1dr.pubg.afteractionreport.core.PlayerAttributes
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerDetailsService
import com.leinb1dr.pubg.afteractionreport.user.User
import com.leinb1dr.pubg.afteractionreport.user.UserService
import com.leinb1dr.pubg.commandgateway.exception.PubgAccountNotFound
import com.leinb1dr.pubg.commandgateway.gateway.events.InteractionPayload
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import javax.lang.model.UnknownEntityException
import javax.lang.model.element.UnknownElementException

@Service
class RegistrationService(
    @Autowired val userService: UserService,
    @Autowired val playerDetailsService: PlayerDetailsService
) {

    fun register(interactionData: InteractionPayload): Mono<User> {
        val discordId = interactionData.member!!.user!!.id
        val discordName = interactionData.member.user!!.username
        val username: String = (interactionData.data?.options?.get(0)?.value as String? ?: discordName)

        return userService.getUserByDiscordId(discordId)
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
}