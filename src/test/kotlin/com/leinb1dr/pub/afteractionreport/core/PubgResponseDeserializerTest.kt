package com.leinb1dr.pub.afteractionreport.core

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PubgResponseDeserializerTest(@Autowired val om: ObjectMapper) {

    @Test
    fun deserializePlayerTest() {
        val readValue = om.readValue(
            ClassLoader.getSystemResource("PlayerSearchResponse.json").readText(),
            PubgWrapper::class.java
        )

        Assertions.assertEquals("player", readValue.data!![0].type)
        Assertions.assertEquals("stealthg0d", (readValue.data!![0].attributes as PlayerAttributes).name)
    }

    @Test
    fun deserializeGetPlayerTest() {
        val readValue = om.readValue(
            ClassLoader.getSystemResource("GetPlayerResponse.json").readText(),
            PubgWrapper::class.java
        )

        Assertions.assertEquals("player", readValue.data!![0].type)
        Assertions.assertEquals("stealthg0d", (readValue.data!![0].attributes as PlayerAttributes).name)
    }

    @Test
    fun deserializeGetSeasonsTest() {
        val readValue = om.readValue(
            ClassLoader.getSystemResource("GetSeasons.json").readText(),
            PubgWrapper::class.java
        )

        Assertions.assertEquals("season", readValue.data!![0].type)
        Assertions.assertFalse((readValue.data!![0].attributes as SeasonAttributes).isOffseason)
    }
}