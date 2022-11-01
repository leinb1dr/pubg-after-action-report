package com.leinb1dr.pub.afteractionreport.report

import com.leinb1dr.pub.afteractionreport.message.MessageService
import com.leinb1dr.pub.afteractionreport.registration.RegistrationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ReportEngine(
    @Autowired val registrationRepository: RegistrationRepository,
    @Autowired val reportService: ReportService,
    @Autowired val messageService: MessageService
) {

    fun checkForReports(timeMilli: Long?): Flux<Boolean> {
        return registrationRepository.findAll()
            .log()
            .buffer(10)
            .flatMap { batch -> reportService.getLatestReport(batch.map { it.pubgId }) }
            .flatMap { messageService.postMessage(it) }
    }

}