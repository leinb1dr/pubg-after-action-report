package com.leinb1dr.pub.afteractionreport.report

import com.leinb1dr.pub.afteractionreport.core.*
import com.leinb1dr.pub.afteractionreport.match.MatchService
import com.leinb1dr.pub.afteractionreport.player.PlayerService
import com.leinb1dr.pub.afteractionreport.seasons.SeasonService
import com.leinb1dr.pub.afteractionreport.usermatch.UserMatch
import com.leinb1dr.pub.afteractionreport.usermatch.UserMatchRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkClass
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

    @MockK
    lateinit var ss: SeasonService

    private lateinit var rs: ReportService

    private val pubgId = "account.0bee6c2ee01d44299425625bcb9e7ddb"
    private val seasonId = "division.bro.official.2017-pre1"

    @BeforeEach
    fun setup() {
        rs = ReportService(ps, userMatchRepository, ms, ss)
        val matchWrapper = PubgWrapper(
            data = arrayOf(
                PubgData(
                    type = "match",
                    id = "2a12fa80-b3a1-48d1-9b75-1a9fd1565ead",
                    attributes = MatchAttributes(
                        OffsetDateTime.now(),
                        0,
                        GameMode.SQUAD_FPP,
                        "Sanhock",
                        false,
                        "",
                        "",
                        "",
                        ""
                    ),
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
                            32.489502,
                            1,
                            "byplayer",
                            0,
                            1,
                            30,
                            1,
                            1,
                            1,
                            2.8585966,
                            "stealthg0d",
                            pubgId,
                            1,
                            0.0,
                            0,
                            0.0,
                            0,
                            399,
                            0,
                            294.12308,
                            2,
                            10
                        ),
                        shardId = "steam"
                    ),
                    relationships = null
                )
            )
        )

        every { ss.getCurrentSeason() } returns currentSeason

        every { ms.getMatch("2a12fa80-b3a1-48d1-9b75-1a9fd1565ead") } returns Mono.just(matchWrapper)
        val seasonStats = mockkClass(SeasonStats::class)
        every { seasonStats.damageDealt } returns 0.0
        every { ps.getPlayerSeasonStats(pubgId, seasonId) } returns Mono.just(
            PubgWrapper(
                data = arrayOf(
                    PubgData(
                        attributes = PlayerSeasonAttributes(mapOf(Pair(GameMode.SQUAD_FPP, seasonStats)))
                    )
                )
            )
        )

        every { ps.findPlayersByIds(listOf(pubgId)) } returns Mono.just(
            PubgWrapper(
                data = arrayOf(
                    PubgData(
                        type = "player",
                        id = pubgId,
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
            userMatchRepository.findOneByPubgId(pubgId)
        } returns Mono.just(oldMatch)

        every {
            userMatchRepository.save(match {
                it.pubgId == pubgId && it.latestMatchId == "2a12fa80-b3a1-48d1-9b75-1a9fd1565ead"
            })
        } returns Mono.just(UserMatch(pubgId = pubgId, latestMatchId = "2a12fa80-b3a1-48d1-9b75-1a9fd1565ead"))

    }

    @Test
    fun createReportTest() {

        val report: Report =
            runBlocking { rs.getLatestReport(arrayListOf(pubgId)).awaitSingle() }

        assertEquals("stealthg0d", report.fields.name)

    }

    @Test
    fun `Report with no season`() {

        every { ss.getCurrentSeason() } returns Mono.empty()

        val report: Report =
            runBlocking { rs.getLatestReport(arrayListOf(pubgId)).awaitSingle() }

        assertEquals("stealthg0d", report.fields.name)

    }

    @Test
    fun noNewMatchTest() {

        every {
            userMatchRepository.findOneByPubgId(pubgId)
        } returns Mono.just(currentMatch)

        assertThrowsExactly(NoSuchElementException::class.java) {
            runBlocking { rs.getLatestReport(arrayListOf(pubgId)).awaitSingle() }
        }

    }

    @Test
    fun `test polling for new match`() {


        every {
            userMatchRepository.findOneByPubgId(pubgId)
        }.returnsMany(Mono.just(currentMatch), Mono.just(currentMatch), Mono.just(currentMatch), Mono.just(oldMatch))

        val report = runBlocking {
            Flux.just(1, 2, 3, 4).timeout(Duration.ofSeconds(1))
                .flatMap { rs.getLatestReport(arrayListOf(pubgId)) }.awaitSingle()
        }

        assertEquals("stealthg0d", report.fields.name)
    }

    @Test
    fun `test polling multiple new matches`() {

        every {
            userMatchRepository.findOneByPubgId(pubgId)
        }.returnsMany(Mono.just(currentMatch), Mono.just(oldMatch), Mono.just(currentMatch), Mono.just(oldMatch))

        assertThrowsExactly(IllegalArgumentException::class.java) {
            runBlocking {
                Flux.just(1, 2, 3, 4).timeout(Duration.ofSeconds(1))
                    .flatMap { rs.getLatestReport(arrayListOf(pubgId)) }.awaitSingle()
            }
        }
    }

    @Test
    fun `test polling and no new match`() {

        every {
            userMatchRepository.findOneByPubgId(pubgId)
        }.returnsMany(Mono.just(currentMatch))

        assertThrowsExactly(NoSuchElementException::class.java) {
            runBlocking {
                Flux.just(1, 2, 3, 4).timeout(Duration.ofSeconds(1))
                    .flatMap { rs.getLatestReport(arrayListOf(pubgId)) }
                    .awaitSingle()
            }
        }
    }

    private val oldMatch = UserMatch(
        pubgId = pubgId,
        latestMatchId = "a0c5d5fa-1fb0-41c0-989f-e52caf06a56d"
    )
    private val currentMatch =
        UserMatch(
            pubgId = pubgId,
            latestMatchId = "2a12fa80-b3a1-48d1-9b75-1a9fd1565ead"
        )


    private val currentSeason = Mono.just(
        PubgData(
            type = "season",
            id = seasonId,
            attributes = SeasonAttributes(isCurrentSeason = true, isOffseason = false)
        )
    )
}