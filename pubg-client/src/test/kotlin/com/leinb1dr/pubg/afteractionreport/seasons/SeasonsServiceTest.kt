package com.leinb1dr.pubg.afteractionreport.seasons

import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.core.SeasonAttributes
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkClass
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Mono

@ExtendWith(MockKExtension::class)
class SeasonsServiceTest {

    @MockK
    lateinit var client: WebClient

    @MockK
    lateinit var responseSpec: WebClient.ResponseSpec
    lateinit var ss: SeasonService

    @BeforeEach
    fun setup() {
        ss = SeasonService(client)

        val headerSpec = mockkClass(WebClient.RequestHeadersUriSpec::class)
        every { client.get() } returns headerSpec
        every { headerSpec.uri("/seasons") } returns headerSpec
        every { headerSpec.retrieve() } returns responseSpec

    }

    @Test
    fun `Get only the current season`() {
        every { responseSpec.toEntity<PubgWrapper>() } returns currentSeason

        val pubgResults = runBlocking { ss.getCurrentSeason().awaitSingle() }

        Assertions.assertEquals("division.bro.official.2017-pre1", pubgResults.id)
        Assertions.assertTrue((pubgResults.attributes as SeasonAttributes).isCurrentSeason)
    }

    @Test
    fun `No current season`() {
        every { responseSpec.toEntity<PubgWrapper>() } returns noCurrentSeason
        Assertions.assertThrowsExactly(NoSuchElementException::class.java) {
            runBlocking { ss.getCurrentSeason().awaitSingle() }
        }

    }

    val currentSeason = Mono.just(
        ResponseEntity(
            PubgWrapper(
                data = arrayOf(
                    PubgData(
                        type = "season",
                        id = "division.bro.official.2017-pre1",
                        attributes = SeasonAttributes(isCurrentSeason = true, isOffseason = false)
                    )
                )
            ), HttpStatus.OK
        )
    )

    val noCurrentSeason = Mono.just(
        ResponseEntity(
            PubgWrapper(
                data = arrayOf(
                    PubgData(
                        type = "season",
                        id = "division.bro.official.2017-pre1",
                        attributes = SeasonAttributes(isCurrentSeason = false, isOffseason = false)
                    )
                )
            ), HttpStatus.OK
        )
    )
}