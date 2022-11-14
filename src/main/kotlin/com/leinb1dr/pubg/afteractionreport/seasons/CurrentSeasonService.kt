package com.leinb1dr.pubg.afteractionreport.seasons

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CurrentSeasonService(@Autowired val repository: CurrentSeasonRepository, @Autowired val ss: SeasonService) {
    fun updateSeason(): Mono<Boolean> = ss.getCurrentSeason()
        .flatMap { currentSeason ->
            repository.existsBySeason(currentSeason.id).flatMap {
                when (it) {
                    false -> repository.save(CurrentSeason(season = currentSeason.id))
                    true -> Mono.empty()
                }
            }
        }
        .map { true }

    fun getCurrentSeason() = repository.findAll().collectList().map { it[0] }


}
