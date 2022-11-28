package com.leinb1dr.pubg.afteractionreport.stats

import com.fasterxml.jackson.databind.ObjectMapper
import com.leinb1dr.pubg.afteractionreport.core.GameMode
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.core.SeasonStats
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeasonRepository
import com.leinb1dr.pubg.afteractionreport.user.User
import com.leinb1dr.pubg.afteractionreport.user.UserService
import io.mockk.every
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono
import java.time.OffsetDateTime

@SpringBootTest
@ActiveProfiles("test")
@Import(com.leinb1dr.pubg.afteractionreport.config.TestConfiguration::class)
class StatsProcessorIntegration {

    @Autowired
    lateinit var matchRepository: com.leinb1dr.pubg.afteractionreport.match.MatchRepository

    @Autowired
    lateinit var currentSeasonRepository: CurrentSeasonRepository

    @Autowired
    lateinit var userService: UserService

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
                    com.leinb1dr.pubg.afteractionreport.match.Match(
                        data =
                        objectMapper.readValue(
                            ClassLoader.getSystemResource("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a.json").readText(),
                            PubgWrapper::class.java
                        ),
                        matchId = "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a"
                    )
                )

        every { userService.getUserByPubgId("account.0bee6c2ee01d44299425625bcb9e7ddb") } returns Mono.just(
            User(discordId = "", pubgId = "account.0bee6c2ee01d44299425625bcb9e7ddb", seasonStats = mapOf(Pair(GameMode.SQUAD_FPP, SeasonStats())))
        )
        every { userService.getUserByPubgId("account.20cf943715ee46328ac1f5eab89cacca") } returns Mono.just(
            User(discordId = "", pubgId = "account.20cf943715ee46328ac1f5eab89cacca", seasonStats = mapOf(Pair(GameMode.SQUAD_FPP, SeasonStats())))        )

        every { userService.getUserByPubgId("account.86f8a9062d1c480a9a5f97ddb2f66280") } returns Mono.just(
            User(discordId = "", pubgId = "account.86f8a9062d1c480a9a5f97ddb2f66280", seasonStats = mapOf(Pair(GameMode.SQUAD_FPP, SeasonStats())))        )

        val reportStats = runBlocking { statsProcessor.collectStats(playerMatch).awaitSingle() }

        assertTrue(OffsetDateTime.now().isAfter(reportStats.second?.matchAttributes!!.createdAt))
    }

}