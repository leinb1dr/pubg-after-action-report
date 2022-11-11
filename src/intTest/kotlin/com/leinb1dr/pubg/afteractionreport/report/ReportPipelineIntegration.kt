package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.user.User
import com.leinb1dr.pubg.afteractionreport.user.UserRepository
import io.mockk.every
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@SpringBootTest
@ActiveProfiles("test")
@Import(com.leinb1dr.pubg.afteractionreport.config.TestConfiguration::class)
class ReportPipelineIntegration(
    @Autowired val userRepository: UserRepository,
    @Autowired val reportPipeline: ReportPipeline
) {

    @Test
    fun `Generate reports`() {
        every { userRepository.findAll() } returns Flux.just(
            User(
                discordId = "",
                pubgId = "account.b668bf9315cb46fca5070402a9f30ee9",
                matchId = ""
            ),
            User(
                discordId = "",
                pubgId = "account.aa9631af0c544f73b09c88b8ddde75f6",
                matchId = ""
            )
        )

        every {
            userRepository.findAndSetMatchIdByPubgId(
                "account.b668bf9315cb46fca5070402a9f30ee9",
                match { true }
            )
        } returns Mono.just(1)
        every {
            userRepository.findAndSetMatchIdByPubgId(
                "account.aa9631af0c544f73b09c88b8ddde75f6",
                match { true }
            )
        } returns Mono.just(1)

        val reportStats = runBlocking { reportPipeline.generateAndSend().collectList().awaitSingle() }

        assertEquals(2, reportStats.size)
        assertEquals("Place", reportStats[0].embeds[0].fields[2].name)
    }

}