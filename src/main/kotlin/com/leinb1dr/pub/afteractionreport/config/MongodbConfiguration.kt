package com.leinb1dr.pub.afteractionreport.config

import com.leinb1dr.pub.afteractionreport.registration.RegistrationRepository
import com.leinb1dr.pub.afteractionreport.seasons.CurrentSeasonRepository
import com.leinb1dr.pub.afteractionreport.usermatch.UserMatchRepository
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories


@Configuration
@EnableReactiveMongoRepositories(
    basePackageClasses = [CurrentSeasonRepository::class, RegistrationRepository::class, UserMatchRepository::class]
)
@Profile("!test")
class MongodbConfiguration