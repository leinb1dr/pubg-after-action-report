package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.core.SeasonStats
import com.leinb1dr.pubg.afteractionreport.match.MatchDetailsService
import io.mockk.mockkClass
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(com.leinb1dr.pubg.afteractionreport.config.TestConfiguration::class)
class ParticipantSearchIntegration(@Autowired private val ms: MatchDetailsService) {

    @Test
    fun createReportTest(){
        val report = runBlocking {
            ms.getMatch("bb70dbd7-631d-4d95-8e9e-fc5c2fdcf55a")
                .participantSearch("account.0bee6c2ee01d44299425625bcb9e7ddb", mockkClass(SeasonStats::class))
                .awaitSingle()
        }

        Assertions.assertEquals("stealthg0d", report.second.stats.name)

    }
}


