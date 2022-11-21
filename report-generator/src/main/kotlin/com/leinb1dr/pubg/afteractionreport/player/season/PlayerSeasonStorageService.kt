package com.leinb1dr.pubg.afteractionreport.player.season

import com.leinb1dr.pubg.afteractionreport.core.GameMode
import com.leinb1dr.pubg.afteractionreport.core.PlayerSeasonAttributes
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class PlayerSeasonStorageService(@Autowired private val playerSeasonRepository: PlayerSeasonRepository) {

    private final val logger = LoggerFactory.getLogger(PlayerSeasonStorageService::class.java)

    fun getSeasonStats(pubgPlayerId: String, gameMode: GameMode) =
        playerSeasonRepository.findByPubgIdAndGameMode(pubgPlayerId, gameMode)
            .doOnError { logger.error("Failed to find season stats, defaulting", it) }
            .defaultIfEmpty(PlayerSeason(pubgId = pubgPlayerId))
            .onErrorReturn(PlayerSeason(pubgId = pubgPlayerId))

    fun saveSeasonStats(pubgPlayerId: String, seasonAttributes: PlayerSeasonAttributes) =
        Flux.fromIterable(seasonAttributes.gameModeStats.entries)
            .flatMap { seasonStats ->
                playerSeasonRepository.findByPubgIdAndGameMode(pubgPlayerId, seasonStats.key)
                    .defaultIfEmpty(PlayerSeason(pubgId = pubgPlayerId, gameMode = seasonStats.key))
                    .flatMap { playerSeasonRepository.save(it.copy(stats = seasonStats.value)) }
            }


}