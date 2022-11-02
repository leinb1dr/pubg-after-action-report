package com.leinb1dr.pub.afteractionreport.report

import com.leinb1dr.pub.afteractionreport.core.*
import com.leinb1dr.pub.afteractionreport.match.MatchService
import com.leinb1dr.pub.afteractionreport.player.PlayerService
import com.leinb1dr.pub.afteractionreport.usermatch.UserMatch
import com.leinb1dr.pub.afteractionreport.usermatch.UserMatchRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.OffsetDateTime

@ExtendWith(MockKExtension::class)
class ReportServiceTest {

    @MockK
    lateinit var userMatchRepository: UserMatchRepository

    @MockK
    lateinit var ps: PlayerService

    @MockK
    lateinit var ms: MatchService

    private lateinit var rs: ReportService

    private val oldMatch = UserMatch(
        pubgId = "account.0bee6c2ee01d44299425625bcb9e7ddb",
        latestMatchId = "a0c5d5fa-1fb0-41c0-989f-e52caf06a56d"
    )
    private val currentMatch =
        UserMatch(
            pubgId = "account.0bee6c2ee01d44299425625bcb9e7ddb",
            latestMatchId = "2a12fa80-b3a1-48d1-9b75-1a9fd1565ead"
        )

    @BeforeEach
    fun setup() {
        rs = ReportService(ps, userMatchRepository, ms)
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

        every { ms.getMatch("2a12fa80-b3a1-48d1-9b75-1a9fd1565ead") } returns Mono.just(matchWrapper)

        every { ps.findPlayersByIds(listOf("account.0bee6c2ee01d44299425625bcb9e7ddb")) } returns Mono.just(
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


        every {
            userMatchRepository.findOneByPubgId("account.0bee6c2ee01d44299425625bcb9e7ddb")
        } returns Mono.just(oldMatch)

        every {
            userMatchRepository.save(match {
                it.pubgId == "account.0bee6c2ee01d44299425625bcb9e7ddb" && it.latestMatchId == "2a12fa80-b3a1-48d1-9b75-1a9fd1565ead"
            })
        } returns Mono.just(UserMatch(pubgId = "account.0bee6c2ee01d44299425625bcb9e7ddb", latestMatchId = "2a12fa80-b3a1-48d1-9b75-1a9fd1565ead"))

    }

    @Test
    fun createReportTest() {

        val report: Report =
            runBlocking { rs.getLatestReport(arrayListOf("account.0bee6c2ee01d44299425625bcb9e7ddb")).awaitSingle() }



        assertEquals("stealthg0d", report.fields.name)

    }

    @Test
    fun noNewMatchTest() {

        every {
            userMatchRepository.findOneByPubgId("account.0bee6c2ee01d44299425625bcb9e7ddb")
        } returns Mono.just(currentMatch)

        assertThrowsExactly(NoSuchElementException::class.java) {
            runBlocking { rs.getLatestReport(arrayListOf("account.0bee6c2ee01d44299425625bcb9e7ddb")).awaitSingle() }
        }

    }

    @Test
    fun `test polling for new match`() {


        every {
            userMatchRepository.findOneByPubgId("account.0bee6c2ee01d44299425625bcb9e7ddb")
        }.returnsMany(Mono.just(currentMatch), Mono.just(currentMatch), Mono.just(currentMatch), Mono.just(oldMatch))

        val report = runBlocking {
            Flux.just(1, 2, 3, 4).timeout(Duration.ofSeconds(1))
                .flatMap { rs.getLatestReport(arrayListOf("account.0bee6c2ee01d44299425625bcb9e7ddb")) }.awaitSingle()
        }

        assertEquals("stealthg0d", report.fields.name)
    }

    @Test
    fun `test polling multiple new matches`() {

        every {
            userMatchRepository.findOneByPubgId("account.0bee6c2ee01d44299425625bcb9e7ddb")
        }.returnsMany(Mono.just(currentMatch), Mono.just(oldMatch), Mono.just(currentMatch), Mono.just(oldMatch))

        assertThrowsExactly(IllegalArgumentException::class.java) {
            runBlocking {
                Flux.just(1, 2, 3, 4).timeout(Duration.ofSeconds(1))
                    .flatMap { rs.getLatestReport(arrayListOf("account.0bee6c2ee01d44299425625bcb9e7ddb")) }.awaitSingle()
            }
        }
    }

    @Test
    fun `test polling and no new match`() {

        every {
            userMatchRepository.findOneByPubgId("account.0bee6c2ee01d44299425625bcb9e7ddb")
        }.returnsMany(Mono.just(currentMatch))

        assertThrowsExactly(NoSuchElementException::class.java) {
            runBlocking {
                Flux.just(1, 2, 3, 4).timeout(Duration.ofSeconds(1))
                    .flatMap { rs.getLatestReport(arrayListOf("account.0bee6c2ee01d44299425625bcb9e7ddb")) }.awaitSingle()
            }
        }
    }
}