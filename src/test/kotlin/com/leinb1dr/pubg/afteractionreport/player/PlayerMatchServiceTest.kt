package com.leinb1dr.pubg.afteractionreport.player

import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatch
import com.leinb1dr.pubg.afteractionreport.player.match.PlayerMatchService
import com.leinb1dr.pubg.afteractionreport.user.User
import com.leinb1dr.pubg.afteractionreport.user.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Flux

@ExtendWith(MockKExtension::class)
class PlayerMatchServiceTest {

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var playerMatchService: PlayerMatchService

    @Test
    fun `Get latest match for all users`() {

        every { userRepository.findAll() } returns listOfUsersWithMatches

        val playerMatches: List<PlayerMatch> =
            runBlocking { playerMatchService.getProcessedPlayerMatches().collectList().awaitSingle() }

        assertEquals(3, playerMatches.size)
        assertEquals("account.0bee6c2ee01d44299425625bcb9e7d00", playerMatches[0].pubgId)
    }

    companion object {
        val listOfUsersWithMatches = Flux.just(
            User(
                discordId = "asdf",
                pubgId = "account.0bee6c2ee01d44299425625bcb9e7d00",
                matchId = "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf500"
            ),
            User(
                discordId = "asdf",
                pubgId = "account.0bee6c2ee01d44299425625bcb9e7d01",
                matchId = "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf501"
            ),
            User(
                discordId = "asdf",
                pubgId = "account.0bee6c2ee01d44299425625bcb9e7d02",
                matchId = "bb70dbd7-631d-4d95-8e9e-fc5c2fdcf502"
            ),
        )

        val listOfUsersWithNoMatches = Flux.just(
            User(discordId = "asdf", pubgId = "account.0bee6c2ee01d44299425625bcb9e7d00", matchId = ""),
            User(discordId = "asdf", pubgId = "account.0bee6c2ee01d44299425625bcb9e7d01", matchId = ""),
            User(discordId = "asdf", pubgId = "account.0bee6c2ee01d44299425625bcb9e7d02", matchId = ""),
        )
    }

}