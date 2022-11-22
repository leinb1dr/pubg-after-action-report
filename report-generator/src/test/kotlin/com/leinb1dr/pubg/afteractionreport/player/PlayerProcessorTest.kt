package com.leinb1dr.pubg.afteractionreport.player

import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerDetailsService
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatchService
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerProcessor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux

@ExtendWith(MockKExtension::class)
class PlayerProcessorTest {

    @MockK
    lateinit var playerMatchService: PlayerMatchService

    @MockK
    lateinit var playerDetailsService: PlayerDetailsService

    @InjectMockKs
    lateinit var playerProcessor: PlayerProcessor

    @Test
    fun `All Players have new matches`() {

        every { playerMatchService.getProcessedPlayerMatches() } returns processedPlayerMatches
        every {
            playerDetailsService.getLatestPlayerMatches(
                match {
                    it.containsAll(
                        listOf(
                            "account.0bee6c2ee01d44299425625bcb9e7d00",
                            "account.0bee6c2ee01d44299425625bcb9e7d01",
                            "account.0bee6c2ee01d44299425625bcb9e7d02"
                        )
                    )
                }

            )
        } returns latestPlayerMatches

        val allPlayersWithChanges = runBlocking { playerProcessor.findAll().collectList().awaitSingle() }
        assertEquals(3, allPlayersWithChanges.size)
    }

    @Test
    fun `No Players have new matches`() {

        every { playerMatchService.getProcessedPlayerMatches() } returns processedPlayerMatchesNoChange
        every {
            playerDetailsService.getLatestPlayerMatches(
                match {
                    it.containsAll(
                        listOf(
                            "account.0bee6c2ee01d44299425625bcb9e7d00",
                            "account.0bee6c2ee01d44299425625bcb9e7d01",
                            "account.0bee6c2ee01d44299425625bcb9e7d02"
                        )
                    )
                }

            )
        } returns latestPlayerMatches


        val result = runBlocking {
            playerProcessor.findAll().collectList().awaitSingle()
        }

        assertEquals(0, result.size)
    }

    private val processedPlayerMatches: Flux<PlayerMatch> =
        PlayerMatchServiceTest.listOfUsersWithNoMatches.map(PlayerMatch::create)

    private val processedPlayerMatchesNoChange: Flux<PlayerMatch> =
        PlayerMatchServiceTest.listOfUsersWithMatches.map(PlayerMatch::create)

    private val latestPlayerMatches: Flux<PubgData> = Flux.fromArray(players.data!!)

    companion object {
        val player =
            PubgWrapper(
                arrayOf(
                    PubgData(
                        "player",
                        "account.0bee6c2ee01d44299425625bcb9e7d00",
                        relationships = mapOf(
                            Pair(
                                "matches", PubgWrapper(
                                    arrayOf(
                                        PubgData("match", "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf500")
                                    )
                                )
                            )
                        )
                    )
                )
            )
        val players =
            PubgWrapper(
                arrayOf(
                    PubgData(
                        "player",
                        "account.0bee6c2ee01d44299425625bcb9e7d00",
                        relationships = mapOf(
                            Pair(
                                "matches", PubgWrapper(
                                    arrayOf(
                                        PubgData("match", "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf500")
                                    )
                                )
                            )
                        )
                    ),
                    PubgData(
                        "player",
                        "account.0bee6c2ee01d44299425625bcb9e7d01",
                        relationships = mapOf(
                            Pair(
                                "matches", PubgWrapper(
                                    arrayOf(
                                        PubgData("match", "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf501")
                                    )
                                )
                            )
                        )
                    ),
                    PubgData(
                        "player",
                        "account.0bee6c2ee01d44299425625bcb9e7d02",
                        relationships = mapOf(
                            Pair(
                                "matches", PubgWrapper(
                                    arrayOf(
                                        PubgData("match", "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf502")
                                    )
                                )
                            )
                        )
                    )
                )
            )

        val playerSeason = PubgWrapper(arrayOf(PubgData("playerSeason")))
    }
}