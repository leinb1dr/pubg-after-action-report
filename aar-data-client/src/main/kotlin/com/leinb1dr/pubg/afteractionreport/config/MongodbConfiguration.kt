package com.leinb1dr.pubg.afteractionreport.config

import MongoOffsetDateTimeWriter
import com.leinb1dr.pubg.afteractionreport.match.MatchRepository
import com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeasonRepository
import com.leinb1dr.pubg.afteractionreport.user.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories(
    basePackageClasses = [CurrentSeasonRepository::class, UserRepository::class, MatchRepository::class]
)
@Profile("!test")
class MongodbConfiguration {
    @Bean
    fun mongoCustomConversions(): MongoCustomConversions {
        return MongoCustomConversions(
            mutableListOf(
                MongoOffsetDateTimeWriter(),
                com.leinb1dr.pubg.afteractionreport.config.converters.MongoOffsetDateTimeReader()
            )
        )
    }
}