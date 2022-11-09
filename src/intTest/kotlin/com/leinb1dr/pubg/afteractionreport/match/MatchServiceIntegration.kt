package com.leinb1dr.pubg.afteractionreport.match

import com.leinb1dr.pubg.afteractionreport.core.ParticipantAttributes
import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.stats.Stats
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.OffsetDateTime

@SpringBootTest
@ActiveProfiles("test")
@Import(com.leinb1dr.pubg.afteractionreport.config.TestConfiguration::class)
class MatchServiceIntegration(@Autowired private val ms: MatchDetailsService) {

    @Test
    fun `Get match details for player`() {
        val playerMatch: PlayerMatch = object : PlayerMatch {
            override val pubgId: String = "account.0bee6c2ee01d44299425625bcb9e7ddb"
            override val matchId: String = "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a"
        }

        val matchStats: Stats = runBlocking {
            ms.getMatchDetailsForPlayer(playerMatch).awaitSingle()
        }

        Assertions.assertTrue(OffsetDateTime.now().isAfter(matchStats.attributes!!.createdAt))
    }

    @Test
    fun findMatchById() {

        val pubgResults = runBlocking { ms.getMatch("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a").awaitSingle() }

        assertEquals("match", pubgResults.data!![0].type)
        assertEquals("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a", pubgResults.data!![0].id)
    }

    @Test
    fun findPlayerParticipant() {

        val pubgResults = runBlocking { ms.getMatch("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a").awaitSingle() }

        assertEquals("match", pubgResults.data!![0].type)
        assertEquals("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a", pubgResults.data!![0].id)
        val find = pubgResults.included!!
            .filter { it.type == "participant" }
            .map { it.attributes as ParticipantAttributes }
            .find { it.stats.playerId == "account.0bee6c2ee01d44299425625bcb9e7ddb" }

        assertEquals("stealthg0d", find!!.stats.name)
    }

}