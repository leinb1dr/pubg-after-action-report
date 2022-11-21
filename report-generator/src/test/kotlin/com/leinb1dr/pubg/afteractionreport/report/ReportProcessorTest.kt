package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.core.AbstractStats
import com.leinb1dr.pubg.afteractionreport.core.MatchAttributes
import com.leinb1dr.pubg.afteractionreport.core.ParticipantStats
import com.leinb1dr.pubg.afteractionreport.core.SeasonStats
import com.leinb1dr.pubg.afteractionreport.player.match.DefaultPlayerMatch
import com.leinb1dr.pubg.afteractionreport.stats.Stats
import com.leinb1dr.pubg.afteractionreport.stats.StatsProcessorContext
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReportProcessorTest {

    private val reportProcessor = ReportProcessor()
    private val statsProcessorContext = StatsProcessorContext()
    private val rawReport = RawReportStats(
        DefaultPlayerMatch("asdf", "asdf"),
        object : Stats {
            override val attributes: MatchAttributes = MatchAttributes()
            override val stats: AbstractStats = ParticipantStats(assists = 2, DBNOs = 1, heals = 1, revives = 2)
        },
        object : Stats {
            override val attributes: MatchAttributes? = null
            override val stats: AbstractStats = SeasonStats(assists = 1, DBNOs = 2, heals = 1, revives = 1)
        }
    )

    @BeforeEach
    fun setup(){
        statsProcessorContext.rawReportStats = rawReport

    }

    private val rawReportBiggerNumbers = RawReportStats(
        DefaultPlayerMatch("asdf", "asdf"),
        object : Stats {
            override val attributes: MatchAttributes = MatchAttributes()
            override val stats: AbstractStats = ParticipantStats(DBNOs = 1)
        },
        object : Stats {
            override val attributes: MatchAttributes? = null
            override val stats: AbstractStats = SeasonStats(DBNOs = 172, roundsPlayed = 66)
        }
    )

    @Test
    fun `Default transform behavior`() {
        val context = runBlocking { reportProcessor.transformReport(statsProcessorContext).awaitSingle() }

        assertEquals(0, context.report!!.fields.kills.value)
        assertEquals(ReportAnnotation.EVEN, context.report!!.fields.kills.annotation)
        assertEquals(-1, context.report!!.fields.heals)

    }

    @Test
    fun `Above annotation transform behavior`() {
        val context = runBlocking { reportProcessor.transformReport(statsProcessorContext).awaitSingle() }

        assertEquals(2, context.report!!.fields.assists.value)
        assertEquals(ReportAnnotation.ABOVE, context.report!!.fields.assists.annotation)
    }

    @Test
    fun `Below annotation transform behavior`() {
        val context = runBlocking { reportProcessor.transformReport(statsProcessorContext).awaitSingle() }

        assertEquals(1, context.report!!.fields.DBNOs.value)
        assertEquals(ReportAnnotation.BELOW, context.report!!.fields.DBNOs.annotation)
    }

    @Test
    fun `Interesting stat transform behavior`() {
        val context = runBlocking { reportProcessor.transformReport(statsProcessorContext).awaitSingle() }

        assertEquals(2, context.report!!.fields.revives)
    }

    @Test
    fun `Test with real numbers transform behavior`() {
        statsProcessorContext.rawReportStats = rawReportBiggerNumbers
        val context = runBlocking { reportProcessor.transformReport(statsProcessorContext).awaitSingle() }

        assertEquals(1, context.report!!.fields.DBNOs.value)
        assertEquals(ReportAnnotation.BELOW, context.report!!.fields.DBNOs.annotation)
    }
}