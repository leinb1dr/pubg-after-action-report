package com.leinb1dr.pub.afteractionreport.report

import com.leinb1dr.pub.afteractionreport.core.MatchAttributes
import com.leinb1dr.pub.afteractionreport.core.ParticipantAttributes
import com.leinb1dr.pub.afteractionreport.core.PubgWrapper
import com.leinb1dr.pub.afteractionreport.core.SeasonStats
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun Mono<PubgWrapper>.participantSearch(
    s: String,
    seasonStats: SeasonStats
): Mono<Triple<PubgWrapper, ParticipantAttributes, SeasonStats>> =
    this.flatMapMany { match: PubgWrapper ->
        Flux.fromArray(match.included!!)
            .filter { it.type == "participant" }
            .map { it.attributes as ParticipantAttributes }
            .filter { it.stats.playerId == s }
            .map { Triple(match, it, seasonStats) }
    }
        .toMono()

fun Flux<Triple<PubgWrapper, ParticipantAttributes, SeasonStats>>.formatReport(): Flux<Report> =
    this.map {
        val stats = it.second.stats
        val fields = ReportFields(
            stats.DBNOs,
            stats.assists,
            BigDecimal.valueOf(stats.damageDealt).setScale(2, RoundingMode.HALF_EVEN).toDouble(),
            if(it.third.damageDealt==0.0) ReportAnnotation.NONE else if ((it.third.damageDealt / it.third.roundsPlayed) >= stats.damageDealt) ReportAnnotation.ABOVE else ReportAnnotation.BELOW,
            stats.deathType,
            stats.headshotKills,
            stats.name,
            stats.kills,
            stats.winPlace
        )
        val matchAttributes = it.first.data!![0].attributes as MatchAttributes

        return@map Report(getMapName(matchAttributes.mapName), formatMatchTime(matchAttributes.createdAt), fields)
    }

fun formatMatchTime(createdAt: OffsetDateTime): String {
    return createdAt
        .atZoneSameInstant(ZoneId.of("America/New_York"))
        .toLocalDateTime()
        .format(DateTimeFormatter.ofPattern("MM/dd/YYYY hh:mm a"))
}

fun getMapName(mapName: String): String {
    return when (mapName) {
        "Baltic_Main" -> "Erangel (Remastered)"
        "Chimera_Main" -> "Paramo"
        "Desert_Main" -> "Miramar"
        "DihorOtok_Main" -> "Vikendi"
        "Erangel_Main" -> "Erangel"
        "Heaven_Main" -> "Haven"
        "Kiki_Main" -> "Deston"
        "Range_Main" -> "Camp Jackal"
        "Savage_Main" -> "Sanhok"
        "Summerland_Main" -> "Karakin"
        "Tiger_Main" -> "Taego"
        else -> mapName
    }
}

