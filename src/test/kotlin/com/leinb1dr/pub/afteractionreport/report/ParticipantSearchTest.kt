package com.leinb1dr.pub.afteractionreport.report

import com.leinb1dr.pub.afteractionreport.match.MatchService
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ParticipantSearchTest(@Autowired private val ms: MatchService) {

    @Test
    fun createReportTest(){

        val report = runBlocking {
            ms.getMatch("a0c5d5fa-1fb0-41c0-989f-e52caf06a56d")
                .participantSearch("account.0bee6c2ee01d44299425625bcb9e7ddb")
                .awaitSingle()
        }

        Assertions.assertEquals("stealthg0d", report.second.stats.name)

    }
}


