package com.leinb1dr.pub.afteractionreport.config

import com.leinb1dr.pub.afteractionreport.registration.RegistrationRepository
import com.leinb1dr.pub.afteractionreport.seasons.CurrentSeasonRepository
import com.leinb1dr.pub.afteractionreport.usermatch.UserMatchRepository
import com.mongodb.reactivestreams.client.MongoClient
import io.mockk.mockkClass
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles

@TestConfiguration
@ActiveProfiles("test")
class TestConfiguration {
    @Bean
    fun currentSeasonRepository() = mockkClass(CurrentSeasonRepository::class)

    @Bean
    fun registrationRepository() = mockkClass(RegistrationRepository::class)

    @Bean
    fun userMatchRepository() = mockkClass(UserMatchRepository::class)

    @Bean
    fun mongoClient() = mockkClass(MongoClient::class)
}