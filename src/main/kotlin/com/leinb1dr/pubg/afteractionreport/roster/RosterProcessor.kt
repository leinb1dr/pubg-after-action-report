package com.leinb1dr.pubg.afteractionreport.roster

import com.leinb1dr.pubg.afteractionreport.core.ParticipantAttributes
import com.leinb1dr.pubg.afteractionreport.core.PubgData
import com.leinb1dr.pubg.afteractionreport.match.Match
import com.leinb1dr.pubg.afteractionreport.player.PlayerMatch
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class RosterProcessor {

    companion object {
        fun extractRoster(match: Match, playerMatch: PlayerMatch) =
            Mono
                .just(match.data.included!!.filter { it.type == "participant" }
                    .filter { (it.attributes as ParticipantAttributes).stats.playerId == playerMatch.pubgId }
                    .map { it.id }
                    .first())
                .map { participantId ->
                    match.data.included.filter { it.type == "roster" }
                        .find { rosterData ->
                            rosterData.relationships!!["participants"]!!.data!!.any { it.id == participantId }
                        }
                }

        fun extractParticipants(match: Match, roster: PubgData?) =
            Flux.fromArray(roster!!.relationships!!["participants"]!!.data!!)
                .map { participant ->
                    match.data.included!!.filter { it.type == "participant" }
                        .find { it.id == participant.id }
                }
    }


}