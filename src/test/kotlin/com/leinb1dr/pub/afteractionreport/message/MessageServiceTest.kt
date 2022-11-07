package com.leinb1dr.pub.afteractionreport.message

import com.leinb1dr.pub.afteractionreport.report.Report
import com.leinb1dr.pub.afteractionreport.report.ReportAnnotation
import com.leinb1dr.pub.afteractionreport.report.ReportFields
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(com.leinb1dr.pub.afteractionreport.config.TestConfiguration::class)
class MessageServiceTest(@Autowired private val ms: MessageService) {

    @Test
    fun `Post Message with interesting param`() {

        val stats =
            Report("Tiger_Main", "10/1/2012", ReportFields(0, 1, 32.489502, ReportAnnotation.ABOVE, "byplayer", 0, "stealthg0d", 1, 27, 3))

        val result = runBlocking { ms.postMessage(stats).awaitSingle() }

        assertEquals("Pubg Match Report", result["embeds"]!![0]["title"] as String)
        assertEquals("3", ((result["embeds"]!![0]["fields"] as List<*>)[8] as Map<*, *>)["value"])
    }

    @Test
    fun `Post Message with default params`() {

        val stats =
            Report("Tiger_Main", "10/1/2012", ReportFields(0, 1, 32.489502, ReportAnnotation.ABOVE, "byplayer", 0, "stealthg0d", 1, 27, -1))

        val result = runBlocking { ms.postMessage(stats).awaitSingle() }
        assertEquals(8,(result["embeds"]!![0]["fields"] as List<*>).size)
        assertNull(((result["embeds"]!![0]["fields"] as List<*>)[7] as Map<*, *>)["Heals Used"])
    }

}