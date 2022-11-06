package com.leinb1dr.pub.afteractionreport

import com.leinb1dr.pub.afteractionreport.config.AppProperties
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Import(com.leinb1dr.pub.afteractionreport.config.TestConfiguration::class)
class AfterActionReportApplicationTests(@Autowired val appProperties: AppProperties) {
}
