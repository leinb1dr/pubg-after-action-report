package com.leinb1dr.pubg.afteractionreport.seasons

import com.leinb1dr.pubg.afteractionreport.core.PubgData
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono

@ExtendWith(MockKExtension::class)
class CurrentSeasonServiceTest {

    @MockK
    lateinit var ss: SeasonService

    @MockK
    lateinit var repository: com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeasonRepository

    lateinit var css: com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeasonService

    @BeforeEach
    fun setup() {
        css = com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeasonService(repository, ss)
    }

    @Test
    fun `Update Current Season`() {

        every { repository.existsBySeason("division.bro.official.2017-pre1") } returns Mono.just(false)
        every { ss.getCurrentSeason() } returns Mono.just(
            PubgData(
                type = "season",
                id = "division.bro.official.2017-pre1"
            )
        )
        every { repository.findByCurrent(true) } returns Mono.just(
            com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeason(
                season = ""
            )
        )

        every { repository.save(match { it.season == "division.bro.official.2017-pre1" }) } returns Mono.just(
            com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeason(season = "division.bro.official.2017-pre1")
        )

        val pubgResults = runBlocking { css.updateSeason().awaitSingle() }
        assertTrue(pubgResults)
    }

    @Test
    fun `Current Season up to date`() {
        every { ss.getCurrentSeason() } returns Mono.just(
            PubgData(
                type = "season",
                id = "division.bro.official.2017-pre1"
            )
        )

        every { repository.existsBySeason("division.bro.official.2017-pre1") } returns Mono.just(true)
        assertThrowsExactly(NoSuchElementException::class.java) { runBlocking { css.updateSeason().awaitSingle() } }

    }
}