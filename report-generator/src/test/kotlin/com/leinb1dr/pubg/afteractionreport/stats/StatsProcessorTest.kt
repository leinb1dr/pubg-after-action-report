package com.leinb1dr.pubg.afteractionreport.stats

import com.leinb1dr.pubg.afteractionreport.core.*
import com.leinb1dr.pubg.afteractionreport.match.Match
import com.leinb1dr.pubg.afteractionreport.match.MatchProcessor
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.player.season.PlayerSeason
import com.leinb1dr.pubg.afteractionreport.player.season.PlayerSeasonStorageService
import com.leinb1dr.pubg.afteractionreport.report.ReportProcessor
import com.mongodb.assertions.Assertions.assertTrue
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono

@ExtendWith(MockKExtension::class)
class StatsProcessorTest {
    @MockK
    lateinit var matchProcessor: MatchProcessor

    @MockK
    lateinit var playerSeasonStorageService: PlayerSeasonStorageService

    @SpyK
    var reportProcessor: ReportProcessor = ReportProcessor()


    @InjectMockKs
    lateinit var statsProcessor: StatsProcessor

    @Test
    fun `Get match details for players`() {
        val playerMatch: PlayerMatch = object : PlayerMatch {
            override val pubgId: String = "account.0bee6c2ee01d44299425625bcb9e7ddb"
            override val matchId: String = "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a"
        }

        every { matchProcessor.lookup(playerMatch) } returns Mono.just(
            Match(
                matchId = "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a", data = PubgWrapper(
                    arrayOf(PubgData(attributes = MatchAttributes(mapName = PubgMap.DESTON))),
                    arrayOf(
                        PubgData(
                            type = "roster",
                            "1",
                            attributes = RosterAttributes(RosterStats(), false, "steam"),
                            mapOf(
                                Pair(
                                    "participants",
                                    PubgWrapper(
                                        arrayOf(
                                            PubgData("participant", "1"),
                                            PubgData("participant", "2")
                                        )
                                    )
                                )
                            )
                        ),
                        PubgData(
                            type = "participant",
                            "1",
                            ParticipantAttributes(
                                ParticipantStats(playerId = "account.0bee6c2ee01d44299425625bcb9e7ddb"),
                                "steam"
                            )
                        ),
                        PubgData(
                            type = "participant",
                            "2",
                            ParticipantAttributes(
                                ParticipantStats(playerId = "account.0bee6c2ee01d44299425625bcb9e7d02"),
                                "steam"
                            )
                        )

                    )
                )
            )
        )

        every {
            playerSeasonStorageService.getSeasonStats("account.0bee6c2ee01d44299425625bcb9e7ddb", match { true })
        } returns Mono.just(PlayerSeason())

        every {
            playerSeasonStorageService.getSeasonStats("account.0bee6c2ee01d44299425625bcb9e7d02", match { true })
        } returns Mono.just(PlayerSeason())


        val reportStats = runBlocking { statsProcessor.collectStats(playerMatch).awaitSingle() }

        assertTrue(reportStats.second != null)

    }

}