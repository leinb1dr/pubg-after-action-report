package com.leinb1dr.pubg.afteractionreport.match

import com.leinb1dr.pubg.afteractionreport.core.MatchAttributes
import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@ExtendWith(MockKExtension::class)
class MatchProcessorTest {

    @MockK
    lateinit var matchService: MatchService

    @InjectMockKs
    lateinit var matchProcessor: MatchProcessor

    @Test
    fun `Get match details for players`() {
        val playerMatch: PlayerMatch = object : PlayerMatch {
            override val pubgId: String = "account.0bee6c2ee01d44299425625bcb9e7ddb"
            override val matchId: String = "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a"
        }

        every {
            matchService.getMatchDetailsForPlayer(match {
                it.matchId == "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a"
                        && it.pubgId == "account.0bee6c2ee01d44299425625bcb9e7ddb"
            }
            )
        } returns Mono.just(object : PlayerMatchStats {
            override val attributes: MatchAttributes = MatchAttributes()
        })

        val matchStats = runBlocking { matchProcessor.lookupDetails(playerMatch).awaitSingle() }

        Assertions.assertTrue(OffsetDateTime.now().isAfter(matchStats.attributes.createdAt))
    }

}