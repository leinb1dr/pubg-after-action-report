package com.leinb1dr.pub.afteractionreport.registration

import com.leinb1dr.pub.afteractionreport.player.PlayerService
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserRegistrationService(
    @Autowired val playerService: PlayerService,
    @Autowired val registrationRepository: RegistrationRepository
) {

    fun registerUser(discordId: String, pubgUserName: String): Mono<RegisteredUser> =
        playerService.findPlayer(pubgUserName)
            .map { it.data!![0].id }
            .map { RegisteredUser(ObjectId.get(), discordId, it) }
            .flatMap { registrationRepository.insert(it) }


    fun getRegisteredUser(discordId: String): Mono<RegisteredUser> = registrationRepository.findById(discordId)

}
