package com.leinb1dr.pubg.afteractionreport.player

import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.util.SetupWebClientMock
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.reactive.function.client.WebClient

@ExtendWith(MockKExtension::class)
class PlayerDetailsServiceTest() {

    @MockK
    lateinit var webClient: WebClient

    @InjectMockKs
    lateinit var ps: PlayerDetailsService

    lateinit var webClientMock: SetupWebClientMock.Builder

    @BeforeEach
    fun setup() {
        webClientMock = SetupWebClientMock.Builder(webClient)
        webClientMock.get().uri {
            every { it.uri("/players?filter[playerNames]={name}", "stealthg0d") } returns it
            every { it.uri("/players/{id}", "account.0bee6c2ee01d44299425625bcb9e7d00") } returns it
            every { it.uri("/players?filter[playerIds]={name}", "account.0bee6c2ee01d44299425625bcb9e7d00") } returns it
            every {
                it.uri(
                    "/players/{pubgId}/seasons/{seasonId}",
                    "account.0bee6c2ee01d44299425625bcb9e7d00",
                    "division.bro.official.pc-2018-20"
                )
            } returns it
        }.retrieve().toEntity()
    }

    @Test
    fun findPlayerByNameTest() {
        webClientMock.body(player)

        val pubgResults = runBlocking { ps.findPlayer("stealthg0d").awaitSingle() }

        assertEquals("player", pubgResults.data!![0].type)
        assertEquals("account.0bee6c2ee01d44299425625bcb9e7d00", pubgResults.data!![0].id)

    }

    @Test
    fun getPlayerMatches() {
        webClientMock.body(player)

        val pubgResults = runBlocking { ps.findPlayer("stealthg0d").awaitSingle() }

        assertEquals("match", pubgResults.data!![0].relationships!!["matches"]!!.data!![0].type)
        assertNotNull(pubgResults.data!![0].relationships!!["matches"]!!.data!![0].id)
    }

    @Test
    fun findPlayerById() {
        webClientMock.body(player)

        val pubgResults = runBlocking { ps.getPlayer("account.0bee6c2ee01d44299425625bcb9e7d00").awaitSingle() }

        assertEquals("player", pubgResults.data!![0].type)
        assertEquals("account.0bee6c2ee01d44299425625bcb9e7d00", pubgResults.data!![0].id)
    }

    @Test
    fun getListOfPlayersById() {
        webClientMock.body(player)


        val pubgResults: PubgWrapper =
            runBlocking {
                ps.findPlayersByIds(arrayListOf("account.0bee6c2ee01d44299425625bcb9e7d00")).awaitSingle()
            }

        assertEquals(1, pubgResults.data!!.size)
        assertEquals("account.0bee6c2ee01d44299425625bcb9e7d00", pubgResults.data!![0].id)
    }

    @Test
    fun `Get Season Stats for Player`() {

        webClientMock.body(playerSeason)

        val pubgResults: PubgWrapper = runBlocking {
            ps.getPlayerSeasonStats(
                "account.0bee6c2ee01d44299425625bcb9e7d00",
                "division.bro.official.pc-2018-20"
            ).awaitSingle()
        }

        assertEquals("playerSeason", pubgResults.data!![0].type)
    }

    @Test
    fun `Get new matches for players`() {
        webClientMock.body(player)

        val playerMatches: List<PlayerMatch> =
            runBlocking { ps.getLatestPlayerMatches(arrayListOf("account.0bee6c2ee01d44299425625bcb9e7d00")).collectList().awaitSingle() }

        assertEquals(1, playerMatches.size)
        assertEquals("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf500", playerMatches[0].matchId)
    }

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