package com.leinb1dr.pubg.afteractionreport.seasons

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CurrentSeasonService(@Autowired val repository: CurrentSeasonRepository, @Autowired val ss: SeasonService) {
    fun updateSeason(): Mono<Boolean> = ss.getCurrentSeason()
            .flatMap { repository.save(CurrentSeason(season = it.id)) }
            .map { true }



}
