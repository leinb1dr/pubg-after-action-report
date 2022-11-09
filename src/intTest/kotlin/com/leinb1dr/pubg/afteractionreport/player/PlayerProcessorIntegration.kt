package com.leinb1dr.pubg.afteractionreport.player

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

@SpringBootTest
@ActiveProfiles("test")
@Import(com.leinb1dr.pubg.afteractionreport.config.TestConfiguration::class)
class PlayerProcessorIntegration(
    @Autowired val playerProcessor: PlayerProcessor,
    @Autowired val userRepository: UserRepository
) {

    @Test
    fun `All players changed`() {
        every { userRepository.findAll() } returns Flux.just(
            User(
                discordId = "",
                pubgId = "account.b668bf9315cb46fca5070402a9f30ee9",
                latestMatchId = ""
            ),
            User(
                discordId = "",
                pubgId = "account.aa9631af0c544f73b09c88b8ddde75f6",
                latestMatchId = ""
            )
        )

        val all = runBlocking { playerProcessor.findAll().collectList().awaitSingle() }

        assertEquals(2, all.size)

    }

}