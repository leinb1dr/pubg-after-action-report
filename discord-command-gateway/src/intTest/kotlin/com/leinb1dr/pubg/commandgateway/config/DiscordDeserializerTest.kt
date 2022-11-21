package com.leinb1dr.pubg.commandgateway.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.leinb1dr.pubg.commandgateway.gateway.events.DiscordEvent
import com.leinb1dr.pubg.commandgateway.gateway.events.Hello
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DiscordDeserializerTest(@Autowired val om: ObjectMapper) {

    @Test
    fun deserializePlayerTest() {
        val readValue = om.readValue(
            ClassLoader.getSystemResource("hello.json").readText(),
            DiscordEvent::class.java
        )

        Assertions.assertTrue(readValue is Hello)
    }
}