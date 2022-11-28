package com.leinb1dr.pubg.afteractionreport.player.season

import com.leinb1dr.pubg.afteractionreport.core.PlayerSeasonAttributes
import com.leinb1dr.pubg.afteractionreport.user.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class PlayerSeasonStatsUpdateProcessor(
    @Autowired private val userService: UserService,
    @Autowired private val currentSeasonService: com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeasonService,
    @Autowired private val playerSeasonService: PlayerSeasonService
) {

    fun process() = currentSeasonService.getCurrentSeason()
        .flatMapMany { currentSeason ->
            userService.getAllUsers()
                .buffer(5)
                .delayElements(Duration.ofSeconds(30))
                .flatMapIterable { it }
                .map { it.pubgId }
                .flatMap { pubgId ->
                    playerSeasonService.getAllPlayerSeasonStats(pubgId, currentSeason.season)
                        .flatMapMany {
                            userService.updateUserSeasonStats(
                                pubgId,
                                (it.data!![0].attributes as PlayerSeasonAttributes).gameModeStats
                            )
                        }
                }
        }

}
