package com.leinb1dr.pub.afteractionreport.match

import com.leinb1dr.pub.afteractionreport.core.ParticipantAttributes
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(com.leinb1dr.pub.afteractionreport.config.TestConfiguration::class)
class MatchServiceTest(@Autowired private val ms: MatchService) {

    @Test
    fun findMatchById() {

        val pubgResults = runBlocking { ms.getMatch("a0c5d5fa-1fb0-41c0-989f-e52caf06a56d").awaitSingle() }

        assertEquals("match", pubgResults.data!![0].type)
        assertEquals("a0c5d5fa-1fb0-41c0-989f-e52caf06a56d", pubgResults.data!![0].id)
    }

    @Test
    fun findPlayerParticipant() {

        val pubgResults = runBlocking { ms.getMatch("a0c5d5fa-1fb0-41c0-989f-e52caf06a56d").awaitSingle() }

        assertEquals("match", pubgResults.data!![0].type)
        assertEquals("a0c5d5fa-1fb0-41c0-989f-e52caf06a56d", pubgResults.data!![0].id)
        val find = pubgResults.included!!
            .filter { it.type == "participant" }
            .map { it.attributes as ParticipantAttributes }
            .find { it.stats.playerId == "account.0bee6c2ee01d44299425625bcb9e7ddb" }

        assertEquals("stealthg0d", find!!.stats.name)
    }

}