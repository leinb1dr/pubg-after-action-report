package com.leinb1dr.pub.afteractionreport.config

import com.leinb1dr.pub.afteractionreport.registration.RegistrationRepository
import com.leinb1dr.pub.afteractionreport.usermatch.UserMatchRepository
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories


@Configuration
@EnableReactiveMongoRepositories(
    basePackageClasses = [RegistrationRepository::class, UserMatchRepository::class]
)
class MongodbConfiguration