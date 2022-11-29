package com.leinb1dr.pubg.commandgateway.gateway.handler

import com.leinb1dr.pubg.commandgateway.exception.PubgAccountNotFound
import com.leinb1dr.pubg.commandgateway.gateway.InteractionResponseService
import com.leinb1dr.pubg.commandgateway.gateway.events.DiscordEvent
import com.leinb1dr.pubg.commandgateway.gateway.events.DiscordMessage
import com.leinb1dr.pubg.commandgateway.gateway.events.Interaction
import com.leinb1dr.pubg.commandgateway.gateway.events.InteractionResponse
import com.leinb1dr.pubg.commandgateway.service.RegistrationService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.reflect.KClass

@Service
class InteractionHandler(
    @Autowired val registrationService: RegistrationService,
    @Autowired val interactionResponseService: InteractionResponseService
) : EventHandler<Interaction> {
    private val logger = LoggerFactory.getLogger(InteractionHandler::class.java)

    override fun handle(it: Interaction): Flux<DiscordEvent> {
        val interactionData = it.d
        logger.info("Dispatch is interaction: $it")

        return registrationService.register(interactionData)
            .map {
                InteractionResponse(
                    4,
                    DiscordMessage(content = ":white_check_mark: You are registered as ${it.pubgName}", flags = (1 shl 6))
                )
            }
            .onErrorResume(PubgAccountNotFound::class.java) {
                Mono.just(
                    InteractionResponse(
                        4,
                        DiscordMessage(
                            content = ":x: Registration failed for ${it.username}.\nTry providing a steam id with the aar command.\nName is case-sensitive and must match steam",
                            flags = (1 shl 6)
                        )
                    )
                )
            }

            .flatMap { response ->
                interactionResponseService.respondToInteraction(
                    interactionData.id.toString(),
                    interactionData.token,
                    response
                )
            }.flatMapMany { Flux.empty() }
    }

    override fun typeHandler(): KClass<Interaction> = Interaction::class
}