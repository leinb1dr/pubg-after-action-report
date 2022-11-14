package com.leinb1dr.pubg.afteractionreport.stats

import com.fasterxml.jackson.databind.ObjectMapper
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.match.Match
import com.leinb1dr.pubg.afteractionreport.match.MatchRepository
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeason
import com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeasonRepository
import io.mockk.every
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@SpringBootTest
@ActiveProfiles("test")
@Import(com.leinb1dr.pubg.afteractionreport.config.TestConfiguration::class)
class StatsProcessorIntegration {

    @Autowired
    lateinit var matchRepository: MatchRepository

    @Autowired
    lateinit var currentSeasonRepository: CurrentSeasonRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var statsProcessor: StatsProcessor

    @Test
    fun `Get match details for players`() {
        val playerMatch: PlayerMatch = object : PlayerMatch {
            override val pubgId: String = "account.0bee6c2ee01d44299425625bcb9e7ddb"
            override val matchId: String = "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a"
        }

        every { matchRepository.existsByMatchId("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a") } returns Mono.just(true)
        every { matchRepository.findByMatchId("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a") } returns
                Mono.just(
                    Match(
                        data =
                        objectMapper.readValue(
                            ClassLoader.getSystemResource("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a.json").readText(),
                            PubgWrapper::class.java
                        ),
                        matchId = "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a"
                    )
                )

        every { currentSeasonRepository.findAll() } returns Flux.just(CurrentSeason(season = "division.bro.official.pc-2018-20"))

        val reportStats = runBlocking { statsProcessor.collectStats(playerMatch).awaitSingle() }

        assertTrue(OffsetDateTime.now().isAfter(reportStats.second?.matchAttributes!!.createdAt))
    }

}