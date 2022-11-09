package com.leinb1dr.pubg.afteractionreport.match

import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.OffsetDateTime

@SpringBootTest
@ActiveProfiles("test")
@Import(com.leinb1dr.pubg.afteractionreport.config.TestConfiguration::class)
class MatchProcessorIntegration(@Autowired val matchProcessor: MatchProcessor) {

    @Test
    fun `Get match details for players`() {
        val playerMatch: PlayerMatch = object : PlayerMatch {
            override val pubgId: String = "account.0bee6c2ee01d44299425625bcb9e7ddb"
            override val matchId: String = "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a"
        }

        val matchStats = runBlocking { matchProcessor.lookupDetails(playerMatch).awaitSingle() }

        Assertions.assertTrue(OffsetDateTime.now().isAfter(matchStats.attributes.createdAt))
    }

}