package com.leinb1dr.pub.afteractionreport.report

import com.leinb1dr.pub.afteractionreport.core.*
import com.leinb1dr.pub.afteractionreport.match.MatchService
import com.leinb1dr.pub.afteractionreport.player.PlayerService
import com.leinb1dr.pub.afteractionreport.usermatch.UserMatch
import com.leinb1dr.pub.afteractionreport.usermatch.UserMatchRepository
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.eq
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.OffsetDateTime

class ReportServiceTest {

    private val userMatchRepository: UserMatchRepository = Mockito.mock(UserMatchRepository::class.java)
    private val ps: PlayerService = Mockito.mock(PlayerService::class.java)
    private val ms: MatchService = Mockito.mock(MatchService::class.java)
    private val rs = ReportService(ps, userMatchRepository, ms)

    private val oldMatch = UserMatch(pubgId = "account.0bee6c2ee01d44299425625bcb9e7ddb", latestMatchId = "a0c5d5fa-1fb0-41c0-989f-e52caf06a56d")
    private val currentMatch =
        UserMatch(pubgId = "account.0bee6c2ee01d44299425625bcb9e7ddb", latestMatchId = "2a12fa80-b3a1-48d1-9b75-1a9fd1565ead")

    @BeforeEach
    fun setup() {
        val matchWrapper = PubgWrapper(
            data = arrayOf(
                PubgData(
                    type = "match",
                    id = "2a12fa80-b3a1-48d1-9b75-1a9fd1565ead",
                    attributes = MatchAttributes(OffsetDateTime.now(), 0, "test", "Sanhock", false, "", "", "", ""),
                    relationships = null
                )
            ),
            included = arrayOf(
                PubgData(
                    type = "participant",
                    id = "2d4cf4d8-a6e2-4295-8b16-b7e1e4da910f",
                    attributes = ParticipantAttributes(
                        stats = ParticipantStats(
                            0,
                            1,
                            32.489502,
                            1,
                            "byplayer",
                            0,
                            1,
                            30,
                            1,
                            1,
                            2.8585966,
                            "stealthg0d",
                            "account.0bee6c2ee01d44299425625bcb9e7ddb",
                            1,
                            0.0,
                            0,
                            0.0,
                            0,
                            399,
                            0,
                            294.12308,
                            2,
                            27
                        ),
                        shardId = "steam"
                    ),
                    relationships = null
                )
            )
        )

        Mockito.`when`(ms.getMatch(anyString()))
            .thenReturn(Mono.just(matchWrapper))

        Mockito.`when`(ps.getPlayer(anyString()))
            .thenReturn(
                Mono.just(
                    PubgWrapper(
                        data = arrayOf(
                            PubgData(
                                type = "player",
                                id = "account.0bee6c2ee01d44299425625bcb9e7ddb",
                                attributes = null,
                                relationships = buildMap {
                                    put(
                                        "matches", matchWrapper
                                    )
                                }
                            )
                        ),
                        included = null
                    )
                )
            )

        Mockito.`when`(userMatchRepository.findById(eq("account.0bee6c2ee01d44299425625bcb9e7ddb")))
            .thenReturn(Mono.just(oldMatch))

    }

    @Test
    fun createReportTest() {

        val report: Report =
            runBlocking { rs.getLatestReport(arrayListOf("account.0bee6c2ee01d44299425625bcb9e7ddb")).awaitSingle() }

        assertEquals("stealthg0d", report.fields.name)

    }

    @Test
    fun noNewMatchTest() {

        Mockito.`when`(userMatchRepository.findById(eq("account.0bee6c2ee01d44299425625bcb9e7ddb")))
            .thenReturn(Mono.just(currentMatch))

        assertThrowsExactly(NoSuchElementException::class.java) {
            runBlocking { rs.getLatestReport(arrayListOf("account.0bee6c2ee01d44299425625bcb9e7ddb")).awaitSingle() }
        }

    }

    @Test
    fun `test polling for new match`() {

        Mockito.`when`(userMatchRepository.findById(eq("account.0bee6c2ee01d44299425625bcb9e7ddb")))
            .thenReturn(Mono.just(currentMatch), Mono.just(currentMatch), Mono.just(currentMatch), Mono.just(oldMatch))

        val report = runBlocking {
            Flux.just(1, 2, 3, 4).timeout(Duration.ofSeconds(1))
                .flatMap { rs.getLatestReport(arrayListOf("account.0bee6c2ee01d44299425625bcb9e7ddb")) }.awaitSingle()
        }

        assertEquals("stealthg0d", report.fields.name)
    }

    @Test
    fun `test polling multiple new matches`() {

        Mockito.`when`(userMatchRepository.findById(eq("account.0bee6c2ee01d44299425625bcb9e7ddb")))
            .thenReturn(Mono.just(currentMatch), Mono.just(oldMatch), Mono.just(currentMatch), Mono.just(oldMatch))

        assertThrowsExactly(IllegalArgumentException::class.java) {
            runBlocking {
                Flux.just(1, 2, 3, 4).timeout(Duration.ofSeconds(1))
                    .flatMap { rs.getLatestReport(arrayListOf("account.0bee6c2ee01d44299425625bcb9e7ddb")) }.awaitSingle()
            }
        }
    }

    @Test
    fun `test polling and no new match`() {

        Mockito.`when`(userMatchRepository.findById(eq("account.0bee6c2ee01d44299425625bcb9e7ddb")))
            .thenReturn(Mono.just(currentMatch))

        assertThrowsExactly(NoSuchElementException::class.java) {
            runBlocking {
                Flux.just(1, 2, 3, 4).timeout(Duration.ofSeconds(1))
                    .flatMap { rs.getLatestReport(arrayListOf("account.0bee6c2ee01d44299425625bcb9e7ddb")) }.awaitSingle()
            }
        }
    }
}