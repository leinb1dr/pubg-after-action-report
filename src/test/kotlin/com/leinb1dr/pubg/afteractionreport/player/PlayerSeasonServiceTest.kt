package com.leinb1dr.pubg.afteractionreport.player

import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.stats.Stats
import com.leinb1dr.pubg.afteractionreport.util.SetupWebClientMock
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
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
                "account.0bee6c2ee01d44299425625bcb9e7d00",
                "division.bro.official.pc-2018-20"
            ).awaitSingle()
        }

        assertNull(pubgResults.attributes)
    }

    companion object {

        val playerSeason = PubgWrapper(arrayOf(PubgData("playerSeason")))
    }
}