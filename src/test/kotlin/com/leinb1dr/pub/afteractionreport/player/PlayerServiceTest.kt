package com.leinb1dr.pub.afteractionreport.player

import com.leinb1dr.pub.afteractionreport.core.PubgWrapper
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(com.leinb1dr.pub.afteractionreport.config.TestConfiguration::class)
class PlayerServiceTest(@Autowired private val ps: PlayerService) {

    @Test
    fun findPlayerByNameTest(){
        val pubgResults = runBlocking { ps.findPlayer("stealthg0d").awaitSingle() }

        assertEquals("player", pubgResults.data!![0].type)
        assertEquals("account.0bee6c2ee01d44299425625bcb9e7ddb", pubgResults.data!![0].id)
    }

    @Test
    fun findPlayerById(){
        val pubgResults = runBlocking { ps.getPlayer("account.0bee6c2ee01d44299425625bcb9e7ddb").awaitSingle() }

        assertEquals("player", pubgResults.data!![0].type)
        assertEquals("account.0bee6c2ee01d44299425625bcb9e7ddb", pubgResults.data!![0].id)
    }

    @Test
    fun getPlayerMatches(){
        val pubgResults = runBlocking { ps.findPlayer("stealthg0d").awaitSingle() }

        assertEquals("match", pubgResults.data!![0].relationships!!["matches"]!!.data!![0].type)
        assertNotNull(pubgResults.data!![0].relationships!!["matches"]!!.data!![0].id)
    }

    @Test
    fun getListOfPlayersById() {
        val pubgResults: PubgWrapper =
            runBlocking { ps.findPlayersByIds(arrayListOf("account.0bee6c2ee01d44299425625bcb9e7ddb")).awaitSingle() }

        assertEquals(1, pubgResults.data!!.size)
        assertEquals("account.0bee6c2ee01d44299425625bcb9e7ddb", pubgResults.data!![0].id)
    }

    @Test
    fun `Get Season Stats for Player`() {
        val pubgResults: PubgWrapper = runBlocking {
            ps.getPlayerSeasonStats(
                "account.0bee6c2ee01d44299425625bcb9e7ddb",
                "division.bro.official.pc-2018-20"
            ).awaitSingle()
        }

        assertEquals("playerSeason", pubgResults.data!![0].type)
    }
}