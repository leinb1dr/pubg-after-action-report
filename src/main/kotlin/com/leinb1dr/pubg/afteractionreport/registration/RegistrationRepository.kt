package com.leinb1dr.pubg.afteractionreport.registration

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface RegistrationRepository: ReactiveMongoRepository<RegisteredUser, String>