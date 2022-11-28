package com.leinb1dr.pubg.afteractionreport.message

import com.leinb1dr.pubg.afteractionreport.core.*
import com.leinb1dr.pubg.afteractionreport.match.Match
import com.leinb1dr.pubg.afteractionreport.report.Report
import com.leinb1dr.pubg.afteractionreport.report.ReportFields
import com.leinb1dr.pubg.afteractionreport.report.TeamReport
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono

@ExtendWith(MockKExtension::class)
internal class MessageProcessorTest {

    @MockK
    lateinit var messageService: MessageService

    @InjectMockKs
    lateinit var messageProcessor: MessageProcessor

    @Test
    fun `Send message solo`() {

        every {
            messageService.postMessage(match {
                it.embeds[0].title == "After Action Report for Deston" &&
                        it.embeds[0].description == "The team placed 3rd and survived for 5.00 minutes" &&
                        it.embeds[0].fields[0].name == "stealthg0d"
            })
        } answers { Mono.just(it.invocation.args[0] as DiscordMessage) }

        val message = runBlocking {
            messageProcessor.sendMessage(
                TeamReport(
                    PubgData("roster", "1234", attributes = RosterAttributes(RosterStats(rank = 3), false, "steam")),
                    com.leinb1dr.pubg.afteractionreport.match.Match(
                        matchId = "asdf",
                        data = PubgWrapper(data = arrayOf(PubgData(attributes = MatchAttributes(mapName = PubgMap.DESTON))))
                    ),
                    listOf(
                        Report(PubgMap.DESTON, "23.2", "stealthg0d", ReportFields(timeSurvived = 5.00))
                    )
                )
            ).awaitSingle()
        }

        assertEquals(1, message.embeds.size)
        assertEquals(1, message.embeds[0].fields.size)
        assertEquals("stealthg0d", message.embeds[0].fields[0].name)
    }

    @Test
    fun `Send message duo`() {

        every {
            messageService.postMessage(match {
                it.embeds[0].title == "After Action Report for Deston" &&
                        it.embeds[0].description == "The team placed 2nd and survived for 6.00 minutes" &&
                        it.embeds[0].fields[0].name == "stealthg0d"
            })
        } answers { Mono.just(it.invocation.args[0] as DiscordMessage) }

        val message = runBlocking {
            messageProcessor.sendMessage(
                TeamReport(
                    PubgData("roster", "1234", attributes = RosterAttributes(RosterStats(rank = 2), false, "steam")),
                    com.leinb1dr.pubg.afteractionreport.match.Match(
                        matchId = "asdf",
                        data = PubgWrapper(data = arrayOf(PubgData(attributes = MatchAttributes(mapName = PubgMap.DESTON))))
                    ),
                    listOf(
                        Report(PubgMap.DESTON, "23.2", "stealthg0d", ReportFields(timeSurvived = 2.00)),
                        Report(PubgMap.DESTON, "23.2", "V0ltexon", ReportFields(timeSurvived = 6.00))
                    )
                )
            ).awaitSingle()
        }

        assertEquals(1, message.embeds.size)
        assertEquals(2, message.embeds[0].fields.size)
        assertEquals("stealthg0d", message.embeds[0].fields[0].name)
    }
}