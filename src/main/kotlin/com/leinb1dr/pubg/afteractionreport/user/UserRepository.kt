package com.leinb1dr.pubg.afteractionreport.user

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface UserRepository: ReactiveMongoRepository<User, String>