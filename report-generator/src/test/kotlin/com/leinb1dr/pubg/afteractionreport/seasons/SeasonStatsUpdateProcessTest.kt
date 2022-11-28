package com.leinb1dr.pubg.afteractionreport.seasons

import com.leinb1dr.pubg.afteractionreport.core.*
import com.leinb1dr.pubg.afteractionreport.player.season.PlayerSeasonService
import com.leinb1dr.pubg.afteractionreport.player.season.PlayerSeasonStatsUpdateProcessor
import com.leinb1dr.pubg.afteractionreport.user.User
import com.leinb1dr.pubg.afteractionreport.user.UserService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@ExtendWith(MockKExtension::class)
class SeasonStatsUpdateProcessTest {

    @MockK
    lateinit var userService: UserService

    @MockK
    lateinit var currentSeasonService: CurrentSeasonService

    @MockK
    lateinit var playerSeasonService: PlayerSeasonService

    @InjectMockKs
    lateinit var seasonStatsUpdateProcess: PlayerSeasonStatsUpdateProcessor

    @Test
    fun `Update season stats`() {

        every { userService.getAllUsers() } returns
                Flux.just(User(discordId = "", pubgId = "1", matchId=""), User(discordId = "", pubgId = "2", matchId=""))
        every { currentSeasonService.getCurrentSeason() } returns Mono.just(
            com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeason(
                season = "season"
            )
        )
        every { playerSeasonService.getAllPlayerSeasonStats("1", "season") } returns
                Mono.just(
                    PubgWrapper(
                        data = arrayOf(
                            PubgData(
                                attributes = PlayerSeasonAttributes(
                                    mapOf(
                                        Pair(
                                            GameMode.SQUAD_FPP,
                                            SeasonStats()
                                        ),
                                        Pair(
                                            GameMode.DUO_FPP,
                                            SeasonStats()
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
        every { playerSeasonService.getAllPlayerSeasonStats("2", "season") } returns Mono.just(
            PubgWrapper(
                data = arrayOf(
                    PubgData(
                        attributes = PlayerSeasonAttributes(
                            mapOf(
                                Pair(
                                    GameMode.SQUAD_FPP,
                                    SeasonStats()
                                )
                            )
                        )
                    )
                )
            )
        )

        every {
            userService.updateUserSeasonStats(
                "1",
                match { true })
        } returns Mono.just(1)

        every {
            userService.updateUserSeasonStats(
                "2",
                match { true })
        } returns Mono.just(1)

        seasonStatsUpdateProcess.process()
    }

}