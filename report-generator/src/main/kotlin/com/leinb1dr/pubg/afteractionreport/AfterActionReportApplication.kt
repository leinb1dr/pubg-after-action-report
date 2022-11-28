package com.leinb1dr.pubg.afteractionreport

import com.leinb1dr.pubg.afteractionreport.config.MongodbConfiguration
import com.leinb1dr.pubg.afteractionreport.config.PubgClientConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import


@SpringBootApplication
@Import(PubgClientConfiguration::class, MongodbConfiguration::class)
class AfterActionReportApplication

fun main(args: Array<String>) {
    runApplication<AfterActionReportApplication>(*args)
}
