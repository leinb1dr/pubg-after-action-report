package com.leinb1dr.pubg.afteractionreport.seasons

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CurrentSeasonService(@Autowired val repository: CurrentSeasonRepository, @Autowired val ss: com.leinb1dr.pubg.afteractionreport.seasons.SeasonService) {
    fun updateSeason(): Mono<Boolean> = ss.getCurrentSeason()
        .flatMap { currentSeason ->
            repository.existsBySeason(currentSeason.id).flatMap { exists ->
                when (exists) {
                    false -> {
                        repository.findByCurrent(true)
                            .defaultIfEmpty(CurrentSeason(season = ""))
                            .flatMap {
                                repository.save(it.copy(season = currentSeason.id))
                            }
                    }
                    true -> Mono.empty()
                }
            }
        }
        .map { true }

    fun getCurrentSeason() = repository.findByCurrent(true)


}