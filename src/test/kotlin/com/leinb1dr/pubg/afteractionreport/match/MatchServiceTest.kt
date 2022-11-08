package com.leinb1dr.pubg.afteractionreport.match

import com.leinb1dr.pubg.afteractionreport.core.*
import com.leinb1dr.pubg.afteractionreport.util.SetupWebClientMock
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.reactive.function.client.WebClient
import java.time.OffsetDateTime

@ExtendWith(MockKExtension::class)
class MatchServiceTest {

    @MockK
    lateinit var webClient: WebClient

    @InjectMockKs
    lateinit var ms: MatchService




    @BeforeEach
    fun setup() {
        SetupWebClientMock
            .Builder(webClient).get().uri {
                every { it.uri("/matches/{id}", "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a") } returns it
            }
            .retrieve().toEntity().body(happyMatch)

    }

    @Test
    fun `Match does not exist`() {
//        every { headerSpec!!.uri(uri, args) } returns headerSpec
        SetupWebClientMock
            .Builder(webClient).get().uri {
                every { it.uri("/matches/{id}", "asdf") } returns it
            }
            .retrieve().toEntity().body(null)

        Assertions.assertThrowsExactly(NoSuchElementException::class.java) {
            runBlocking { ms.getMatch("asdf").awaitSingle() }
        }
    }

    @Test
    fun findMatchById() {

        val pubgResults = runBlocking { ms.getMatch("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a").awaitSingle() }

        assertEquals("match", pubgResults.data!![0].type)
        assertEquals("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a", pubgResults.data!![0].id)
        assertTrue(OffsetDateTime.now().isAfter((pubgResults.data!![0].attributes as MatchAttributes).createdAt))
    }

    @Test
    fun findPlayerParticipant() {

        val pubgResults = runBlocking { ms.getMatch("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a").awaitSingle() }

        assertEquals("match", pubgResults.data!![0].type)
        assertEquals("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a", pubgResults.data!![0].id)
        val find = pubgResults.included!!
            .filter { it.type == "participant" }
            .map { it.attributes as ParticipantAttributes }
            .find { it.stats.playerId == "account.0bee6c2ee01d44299425625bcb9e7ddb" }

        assertEquals("stealthg0d", find!!.stats.name)
    }

    companion object {
        val happyMatch =
            PubgWrapper(
                arrayOf(
                    PubgData(
                        "match",
                        "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a",
                        MatchAttributes()
                    )
                ),
                arrayOf(
                    PubgData(
                        "participant",
                        "account.0bee6c2ee01d44299425625bcb9e7ddb",
                        attributes = ParticipantAttributes(
                            ParticipantStats(
                                name = "stealthg0d",
                                playerId = "account.0bee6c2ee01d44299425625bcb9e7ddb"
                            ), "steam"
                        )
                    )
                )
            )
    }

}