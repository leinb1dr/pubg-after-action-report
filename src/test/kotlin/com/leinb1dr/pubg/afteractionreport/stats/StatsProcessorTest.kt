package com.leinb1dr.pubg.afteractionreport.stats

import com.leinb1dr.pubg.afteractionreport.core.*
import com.leinb1dr.pubg.afteractionreport.match.MatchDetailsService
import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.player.PlayerSeasonService
import com.leinb1dr.pubg.afteractionreport.seasons.SeasonService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@ExtendWith(MockKExtension::class)
class StatsProcessorTest {
    @MockK
    lateinit var matchDetailsService: MatchDetailsService

    @MockK
    lateinit var seasonService: SeasonService

    @MockK
    lateinit var playerSeasonService: PlayerSeasonService

    @InjectMockKs
    lateinit var statsProcessor: StatsProcessor

    @Test
    fun `Get match details for players`() {
        val playerMatch: PlayerMatch = object : PlayerMatch {
            override val pubgId: String = "account.0bee6c2ee01d44299425625bcb9e7ddb"
            override val matchId: String = "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a"
        }

        every {
            matchDetailsService.getMatchDetailsForPlayer(match {
                it.matchId == "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a"
                        && it.pubgId == "account.0bee6c2ee01d44299425625bcb9e7ddb"
            }
            )
        } returns Mono.just(object : Stats {
            override val attributes: MatchAttributes = MatchAttributes()
            override val stats: AbstractStats = ParticipantStats()
        })
        every { seasonService.getCurrentSeason() } returns Mono.just(
            PubgData(
                type = "season",
                id = "division.bro.official.2017-pre1"
            )
        )
        every {
            playerSeasonService.getPlayerSeasonStats(
                object: PlayerMatch {
                    override val pubgId: String = "account.0bee6c2ee01d44299425625bcb9e7d00"
                    override val matchId: String = "asdf"
                },
                "division.bro.official.pc-2018-20",
                GameMode.SOLO_FPP
            )
        } returns Mono.just(object : Stats {
            override val attributes: MatchAttributes? = null
            override val stats: AbstractStats = SeasonStats()
        })


        val reportStats = runBlocking { statsProcessor.collectStats(playerMatch).awaitSingle() }

        assertTrue(OffsetDateTime.now().isAfter(reportStats.matchStats.attributes!!.createdAt))
    }

}