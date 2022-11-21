package com.leinb1dr.pubg.afteractionreport.player

import com.leinb1dr.pubg.afteractionreport.core.GameMode
import com.leinb1dr.pubg.afteractionreport.core.PlayerSeasonAttributes
import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.player.season.PlayerSeasonService
import com.leinb1dr.pubg.afteractionreport.stats.Stats
import com.leinb1dr.pubg.afteractionreport.util.SetupWebClientMock
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.reactive.function.client.WebClient

@ExtendWith(MockKExtension::class)
class PlayerSeasonServiceTest() {

    @MockK
    lateinit var webClient: WebClient

    @InjectMockKs
    lateinit var ps: PlayerSeasonService

    lateinit var webClientMock: SetupWebClientMock.Builder

    @BeforeEach
    fun setup() {
        webClientMock = SetupWebClientMock.Builder(webClient)
        webClientMock.get().uri {
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
    fun `Get Season Stats for Player`() {

        webClientMock.body(playerSeason)

        val pubgResults: Stats = runBlocking {
            ps.getPlayerSeasonStats(
                object : PlayerMatch {
                    override val pubgId: String = "account.0bee6c2ee01d44299425625bcb9e7d00"
                    override val matchId: String = "asdf"
                },
                "division.bro.official.pc-2018-20",
                GameMode.SOLO_FPP
            ).awaitSingle()
        }

        assertNull(pubgResults.attributes)
    }

    companion object {

        val playerSeason = PubgWrapper(arrayOf(PubgData("playerSeason", attributes = PlayerSeasonAttributes(mapOf()))))
    }
}