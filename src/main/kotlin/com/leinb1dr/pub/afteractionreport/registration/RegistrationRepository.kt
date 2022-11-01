package com.leinb1dr.pub.afteractionreport.registration

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface RegistrationRepository: ReactiveMongoRepository<RegisteredUser, String>