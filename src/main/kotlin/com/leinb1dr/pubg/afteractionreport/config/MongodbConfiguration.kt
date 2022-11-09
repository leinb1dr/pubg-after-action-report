package com.leinb1dr.pubg.afteractionreport.config

import com.leinb1dr.pubg.afteractionreport.user.UserRepository
import com.leinb1dr.pubg.afteractionreport.seasons.CurrentSeasonRepository
import com.leinb1dr.pubg.afteractionreport.usermatch.UserMatchRepository
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories


@Configuration
@EnableReactiveMongoRepositories(
    basePackageClasses = [CurrentSeasonRepository::class, UserRepository::class, UserMatchRepository::class]
)
@Profile("!test")
class MongodbConfiguration