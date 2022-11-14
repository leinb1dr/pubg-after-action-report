package com.leinb1dr.pubg.afteractionreport.match

import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.player.match.DefaultPlayerMatch
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrowsExactly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono

@ExtendWith(MockKExtension::class)
internal class MatchProcessorTest {

    @MockK
    private lateinit var matchStorageService: MatchStorageService

    @MockK
    private lateinit var matchDetailsService: MatchDetailsService

    @InjectMockKs
    private lateinit var matchProcessor: MatchProcessor

    @Test
    fun `Process match - exists`() {
        every { matchStorageService.matchExists("asdf") } returns Mono.just(true)

        assertThrowsExactly(
            NoSuchElementException::class.java
        ) { runBlocking { matchProcessor.process("asdf").awaitSingle() } }
    }

    @Test
    fun `Process match - new match`() {
        val matchToStore = PubgWrapper()
        every { matchStorageService.matchExists("asdf") } returns Mono.just(false)
        every { matchDetailsService.getMatch("asdf") } returns Mono.just(matchToStore)
        every { matchStorageService.storeMatch(matchToStore) } returns Mono.just(Match(data=matchToStore, matchId = "asdf"))

        val match: Match = runBlocking { matchProcessor.process("asdf").awaitSingle() }
        assertEquals(matchToStore, match.data)
    }

    @Test
    fun `Lookup - match doesn't exist`() {
        every { matchStorageService.matchExists("asdf") } returns Mono.just(false)
        assertThrowsExactly(
            NoSuchElementException::class.java
        ) { runBlocking { matchProcessor.lookup(DefaultPlayerMatch("123", "asdf")).awaitSingle() } }
    }

    @Test
    fun `Lookup - match exists`() {
        every { matchStorageService.matchExists("asdf") } returns Mono.just(true)
        every { matchStorageService.getMatch("asdf") } returns Mono.just(Match(data = PubgWrapper(), matchId = "asdf"))
        runBlocking { matchProcessor.lookup(DefaultPlayerMatch("123", "asdf")).awaitSingle() }

    }
}