package com.leinb1dr.pub.afteractionreport.seasons

import com.leinb1dr.pub.afteractionreport.core.PubgData
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
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
    lateinit var repository: CurrentSeasonRepository

    lateinit var css: CurrentSeasonService

    @BeforeEach
    fun setup() {
        css = CurrentSeasonService(repository, ss)
    }

    @Test
    fun `Update Current Season`() {

        every { ss.getCurrentSeason() } returns Mono.just(PubgData(type = "season", id="division.bro.official.2017-pre1"))

        every { repository.save(match { it.season == "division.bro.official.2017-pre1" }) } returns Mono.just(
            CurrentSeason(season = "division.bro.official.2017-pre1")
        )

        val pubgResults = runBlocking { css.updateSeason().awaitSingle() }
        assertTrue(pubgResults)
    }
}