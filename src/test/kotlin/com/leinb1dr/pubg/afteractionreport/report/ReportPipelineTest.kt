package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.core.AbstractStats
import com.leinb1dr.pubg.afteractionreport.core.MatchAttributes
import com.leinb1dr.pubg.afteractionreport.core.ParticipantStats
import com.leinb1dr.pubg.afteractionreport.core.SeasonStats
import com.leinb1dr.pubg.afteractionreport.message.DiscordMessage
import com.leinb1dr.pubg.afteractionreport.message.MessageEmbed
import com.leinb1dr.pubg.afteractionreport.message.MessageFields
import com.leinb1dr.pubg.afteractionreport.message.MessageProcessor
import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.player.PlayerProcessor
import com.leinb1dr.pubg.afteractionreport.stats.Stats
import com.leinb1dr.pubg.afteractionreport.stats.StatsProcessor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkClass
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@ExtendWith(MockKExtension::class)
class ReportPipelineTest {

    @MockK
    lateinit var playerProcessor: PlayerProcessor

    @MockK
    lateinit var statsProcessor: StatsProcessor

    @MockK
    lateinit var reportProcessor: ReportProcessor

    @MockK
    lateinit var messageProcessor: MessageProcessor

    @InjectMockKs
    lateinit var reportPipeline: ReportPipeline

    @Test
    fun `Generate reports`() {
        val playerMatch = object : PlayerMatch {
            override val pubgId: String = "account.0bee6c2ee01d44299425625bcb9e7ddb"
            override val matchId: String = "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a"
        }

        every { playerProcessor.findAll() } returns Flux.just(playerMatch)


        val rawReport = RawReportStats(playerMatch, object : Stats {
            override val attributes: MatchAttributes = MatchAttributes()
            override val stats: AbstractStats = ParticipantStats()
        }, object : Stats {
            override val attributes: MatchAttributes? = null
            override val stats: AbstractStats = SeasonStats()
        })

        every { statsProcessor.collectStats(playerMatch) } returns
                Mono.just(rawReport)

        val report = Report("", "", fields = mockkClass(ReportFields::class))
        every { reportProcessor.transformReport(rawReport) } returns Mono.just(report)

        val discordMessage = DiscordMessage(
            arrayOf(
                MessageEmbed(
                    "", "",
                    mutableListOf(MessageFields("", ""))
                )
            )
        )
        every { messageProcessor.sendMessage(report) } returns Mono.just(discordMessage)

        every { playerProcessor.updatePlayerMatch(match { true }) } returns Mono.just(1L)

        val reportStats = runBlocking { reportPipeline.generateAndSend().collectList().awaitSingle() }

        assertEquals(1, reportStats.size)
    }

}