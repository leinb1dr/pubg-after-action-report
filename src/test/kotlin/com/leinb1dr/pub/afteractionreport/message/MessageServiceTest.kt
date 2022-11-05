package com.leinb1dr.pub.afteractionreport.message

import com.leinb1dr.pub.afteractionreport.report.Report
import com.leinb1dr.pub.afteractionreport.report.ReportAnnotation
import com.leinb1dr.pub.afteractionreport.report.ReportFields
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MessageServiceTest(@Autowired private val ms: MessageService) {

    @Test
    fun postMessage() {

        val stats =
            Report("Tiger_Main", "10/1/2012", ReportFields(0, 1, 32.489502, ReportAnnotation.ABOVE, "byplayer", 0, "stealthg0d", 1, 27))

        val result: Boolean = runBlocking { ms.postMessage(stats).awaitSingle() }

        assertTrue(result)
    }

}