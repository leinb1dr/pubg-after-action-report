package com.leinb1dr.pubg.afteractionreport.registration

import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.player.PlayerDetailsService
import com.leinb1dr.pubg.afteractionreport.user.User
import com.leinb1dr.pubg.afteractionreport.user.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono

@ExtendWith(MockKExtension::class)
class UserRegistrationServiceTest {

    @MockK
    lateinit var userRepository: UserRepository
    @MockK
    lateinit var playerDetailsService: PlayerDetailsService
    lateinit var urs: UserRegistrationService

    @BeforeEach
    fun setup(){
        urs = UserRegistrationService(playerDetailsService, userRepository)
    }

    @Test
    fun registerUserTest() {
        every { userRepository.insert(match {
                it: User -> it.discordId=="358394398511202315" && it.pubgId == "account.0bee6c2ee01d44299425625bcb9e7ddb" } as User)
        } returns Mono.just(
                    User(
                        discordId = "358394398511202315",
                        pubgId = "account.0bee6c2ee01d44299425625bcb9e7ddb",
                        latestMatchId = ""
                    )
                )

        every { playerDetailsService.findPlayer("stealthg0d") } returns
                Mono.just(
                    PubgWrapper(
                        data = arrayOf(
                            PubgData(
                                type = "Player",
                                id = "account.0bee6c2ee01d44299425625bcb9e7ddb"
                            )
                        )
                    )
                )


        val user: User =
            runBlocking { urs.registerUser("358394398511202315", "stealthg0d").awaitSingle() }

        assertEquals("account.0bee6c2ee01d44299425625bcb9e7ddb", user.pubgId)
    }

    @Test
    fun getRegisteredUserTest() {
        every { userRepository.findById("358394398511202315") } returns Mono.just(User(discordId = "358394398511202315", pubgId = "account.0bee6c2ee01d44299425625bcb9e7ddb", latestMatchId = ""))


        val user: User = runBlocking { urs.getRegisteredUser("358394398511202315").awaitSingle() }

        assertEquals("account.0bee6c2ee01d44299425625bcb9e7ddb", user.pubgId)
    }
}