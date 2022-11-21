package com.leinb1dr.pubg.afteractionreport.player.match

import com.leinb1dr.pubg.afteractionreport.match.NewMatchLookupTask
import com.leinb1dr.pubg.afteractionreport.stats.TeamStatsTask
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NewPlayerMatchPipeline(
    @Autowired val playerProcessor: PlayerProcessor,
    @Autowired val newMatchLookupTask: NewMatchLookupTask,
    @Autowired val teamStatsTask: TeamStatsTask
) {

    private val logger = LoggerFactory.getLogger(NewPlayerMatchPipeline::class.java)

    fun generateAndSend() =
        playerProcessor.findAll()
            .doOnNext { newMatchLookupTask.lookupMatch(it.matchId) }
            .doOnNext { teamStatsTask.addPlayerMatchToProcess(it) }
            .flatMap { playerMatch ->
                playerProcessor.updatePlayerMatch(playerMatch).onErrorReturn(1L).map { playerMatch }
            }
}


