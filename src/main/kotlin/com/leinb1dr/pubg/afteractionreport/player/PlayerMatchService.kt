package com.leinb1dr.pubg.afteractionreport.player

import com.leinb1dr.pubg.afteractionreport.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class PlayerMatchService(@Autowired private val userRepository: UserRepository) {
    fun getProcessedPlayerMatches(): Flux<PlayerMatch> = userRepository.findAll().map(PlayerMatch::create)
}