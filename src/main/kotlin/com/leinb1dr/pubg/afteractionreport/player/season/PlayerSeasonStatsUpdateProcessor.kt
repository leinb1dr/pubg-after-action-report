package com.leinb1dr.pubg.afteractionreport.player.season

import com.leinb1dr.pubg.afteractionreport.core.PlayerSeasonAttributes
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatchService
import com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeasonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class PlayerSeasonStatsUpdateProcessor(
    @Autowired private val playerMatchService: PlayerMatchService,
    @Autowired private val currentSeasonService: CurrentSeasonService,
    @Autowired private val playerSeasonService: PlayerSeasonService,
    @Autowired private val playerSeasonStorageService: PlayerSeasonStorageService
) {

    fun process() = currentSeasonService.getCurrentSeason()
        .flatMapMany { currentSeason->
            playerMatchService.getProcessedPlayerMatches()
                .buffer(5)
                .delayElements(Duration.ofSeconds(30))
                .flatMapIterable { it }
                .map { it.pubgId }
                .flatMap { pubgId ->
                    playerSeasonService.getAllPlayerSeasonStats(pubgId, currentSeason.season)
                        .flatMapMany { playerSeasonStorageService.saveSeasonStats(pubgId, it.data!![0].attributes as PlayerSeasonAttributes) }
                }
        }

}
