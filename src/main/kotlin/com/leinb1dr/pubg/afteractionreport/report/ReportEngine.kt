package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.message.MessageService
import com.leinb1dr.pubg.afteractionreport.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class ReportEngine(
    @Autowired val userRepository: UserRepository,
    @Autowired val reportService: ReportService,
    @Autowired val messageService: MessageService
) {

    fun checkForReports(timeMilli: Long?): Flux<Map<String, Array<Map<String, Any>>>> {
        return userRepository.findAll()
            .log()
            .buffer(10)
            .flatMap { batch -> reportService.getLatestReport(batch.map { it.pubgId }) }
            .flatMap { messageService.postMessage(it) }
    }

}