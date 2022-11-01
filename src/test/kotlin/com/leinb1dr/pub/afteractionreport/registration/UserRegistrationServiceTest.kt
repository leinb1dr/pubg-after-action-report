package com.leinb1dr.pub.afteractionreport.registration

import com.leinb1dr.pub.afteractionreport.player.PlayerService
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono

@SpringBootTest
class UserRegistrationServiceTest(
    @Autowired private val playerService: PlayerService,
) {
    private val registrationRepository: RegistrationRepository = Mockito.mock(RegistrationRepository::class.java)
    private val urs = UserRegistrationService(playerService, registrationRepository)

    @Test
    fun registerUserTest() {

        Mockito.`when`(registrationRepository.insert(any(RegisteredUser::class.java))
        ).thenReturn(
            Mono.just(RegisteredUser(discordId = "358394398511202315", pubgId = "account.0bee6c2ee01d44299425625bcb9e7ddb"))
        )

        val registeredUser: RegisteredUser =
            runBlocking { urs.registerUser("358394398511202315", "stealthg0d").awaitSingle() }

        assertEquals("account.0bee6c2ee01d44299425625bcb9e7ddb", registeredUser.pubgId)
    }

    @Test
    fun getRegisteredUserTest() {
        Mockito.`when`(registrationRepository.findById(eq("358394398511202315"))
        ).thenReturn(
            Mono.just(RegisteredUser(discordId = "358394398511202315", pubgId = "account.0bee6c2ee01d44299425625bcb9e7ddb"))
        )

        val registeredUser: RegisteredUser = runBlocking { urs.getRegisteredUser("358394398511202315").awaitSingle() }

        assertEquals("account.0bee6c2ee01d44299425625bcb9e7ddb", registeredUser.pubgId)
    }
}