package com.leinb1dr.pubg.afteractionreport.registration

import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import com.leinb1dr.pubg.afteractionreport.player.PlayerService
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
    lateinit var registrationRepository: RegistrationRepository
    @MockK
    lateinit var playerService: PlayerService
    lateinit var urs:UserRegistrationService

    @BeforeEach
    fun setup(){
        urs = UserRegistrationService(playerService, registrationRepository)
    }

    @Test
    fun registerUserTest() {
        every { registrationRepository.insert(match {
                it:RegisteredUser -> it.discordId=="358394398511202315" && it.pubgId == "account.0bee6c2ee01d44299425625bcb9e7ddb" } as RegisteredUser)
        } returns Mono.just(
                    RegisteredUser(
                        discordId = "358394398511202315",
                        pubgId = "account.0bee6c2ee01d44299425625bcb9e7ddb"
                    )
                )

        every { playerService.findPlayer("stealthg0d") } returns
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


        val registeredUser: RegisteredUser =
            runBlocking { urs.registerUser("358394398511202315", "stealthg0d").awaitSingle() }

        assertEquals("account.0bee6c2ee01d44299425625bcb9e7ddb", registeredUser.pubgId)
    }

    @Test
    fun getRegisteredUserTest() {
        every { registrationRepository.findById("358394398511202315") } returns Mono.just(RegisteredUser(discordId = "358394398511202315", pubgId = "account.0bee6c2ee01d44299425625bcb9e7ddb"))


        val registeredUser: RegisteredUser = runBlocking { urs.getRegisteredUser("358394398511202315").awaitSingle() }

        assertEquals("account.0bee6c2ee01d44299425625bcb9e7ddb", registeredUser.pubgId)
    }
}