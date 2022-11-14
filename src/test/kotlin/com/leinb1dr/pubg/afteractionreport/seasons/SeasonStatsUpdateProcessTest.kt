package com.leinb1dr.pubg.afteractionreport.seasons

import com.leinb1dr.pubg.afteractionreport.core.*
import com.leinb1dr.pubg.afteractionreport.player.match.DefaultPlayerMatch
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatchService
import com.leinb1dr.pubg.afteractionreport.player.season.PlayerSeason
import com.leinb1dr.pubg.afteractionreport.player.season.PlayerSeasonService
import com.leinb1dr.pubg.afteractionreport.player.season.PlayerSeasonStatsUpdateProcessor
import com.leinb1dr.pubg.afteractionreport.player.season.PlayerSeasonStorageService
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
    lateinit var playerMatchService: PlayerMatchService

    @MockK
    lateinit var currentSeasonService: CurrentSeasonService

    @MockK
    lateinit var playerSeasonService: PlayerSeasonService

    @MockK
    lateinit var playerSeasonStorageService: PlayerSeasonStorageService

    @InjectMockKs
    lateinit var seasonStatsUpdateProcess: PlayerSeasonStatsUpdateProcessor

    @Test
    fun `Update season stats`() {

        every { playerMatchService.getProcessedPlayerMatches() } returns
                Flux.just(DefaultPlayerMatch("1", ""), DefaultPlayerMatch("2", ""))
        every { currentSeasonService.getCurrentSeason() } returns Mono.just(CurrentSeason(season = "season"))
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
            playerSeasonStorageService.saveSeasonStats(
                "1",
                match { true })
        } returns Flux.just(
            PlayerSeason(pubgId = "1", gameMode = GameMode.SQUAD_FPP),
            PlayerSeason(pubgId = "1", gameMode = GameMode.DUO_FPP)
        )

        every {
            playerSeasonStorageService.saveSeasonStats(
                "2",
                match { true })
        } returns Flux.just(
            PlayerSeason(pubgId = "1", gameMode = GameMode.SQUAD_FPP)
        )

        seasonStatsUpdateProcess.process()
    }

}