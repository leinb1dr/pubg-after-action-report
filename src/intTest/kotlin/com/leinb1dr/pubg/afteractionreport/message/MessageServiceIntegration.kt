package com.leinb1dr.pubg.afteractionreport.message

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(com.leinb1dr.pubg.afteractionreport.config.TestConfiguration::class)
class MessageServiceIntegration(@Autowired private val ms: MessageService) {

    @Test
    fun `Post Message`() {

        val stats =
            DiscordMessage(arrayOf(MessageEmbed("Message Int Test", "Verify posting works", mutableListOf(
                MessageFields("field 1", "value 1")
            ))))

        val result = runBlocking { ms.postMessage(stats).awaitSingle() }

        assertEquals("Message Int Test", result.embeds[0].title)
    }
}