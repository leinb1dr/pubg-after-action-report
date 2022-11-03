package com.leinb1dr.pub.afteractionreport

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.stackdriver.StackdriverConfig
import io.micrometer.stackdriver.StackdriverMeterRegistry
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class AfterActionReportApplication{
    @Bean
    fun meterRegistry(): MeterRegistry? {
        val stackdriverConfig: StackdriverConfig = object : StackdriverConfig {
            override fun get(key: String): String? {
                return null
            }

            override fun projectId(): String {
                return "leinb1dr-test-application-1"
            }
        }
        return StackdriverMeterRegistry.builder(stackdriverConfig).build()
    }
}

fun main(args: Array<String>) {
    runApplication<AfterActionReportApplication>(*args)
}
