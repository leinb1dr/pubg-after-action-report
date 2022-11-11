package com.leinb1dr.pubg.afteractionreport.config

import com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeasonRepository
import com.leinb1dr.pubg.afteractionreport.user.UserRepository
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
    fun registrationRepository() = mockkClass(UserRepository::class)


    @Bean
    fun mongoClient() = mockkClass(MongoClient::class)

}