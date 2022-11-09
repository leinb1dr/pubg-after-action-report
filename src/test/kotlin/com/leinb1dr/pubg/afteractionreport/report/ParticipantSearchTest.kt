package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.core.SeasonStats
import com.leinb1dr.pubg.afteractionreport.match.MatchDetailsService
import com.leinb1dr.pubg.afteractionreport.match.MatchServiceTest
import com.leinb1dr.pubg.afteractionreport.util.SetupWebClientMock
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkClass
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.web.reactive.function.client.WebClient

@ExtendWith(MockKExtension::class)
class ParticipantSearchTest {

    @MockK
    lateinit var webClient: WebClient

    @InjectMockKs
    lateinit var ms: MatchDetailsService

    @BeforeEach
    fun setup() {
        SetupWebClientMock
            .Builder(webClient).get().uri {
                every { it.uri("/matches/{id}", "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a") } returns it
            }
            .retrieve().toEntity().body(MatchServiceTest.happyMatch)
    }

    @Test
    fun createReportTest() {
        val report = runBlocking {
            ms.getMatch("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a")
                .participantSearch("account.0bee6c2ee01d44299425625bcb9e7ddb", mockkClass(SeasonStats::class))
                .awaitSingle()
        }

        Assertions.assertEquals("stealthg0d", report.second.stats.name)

    }
}


