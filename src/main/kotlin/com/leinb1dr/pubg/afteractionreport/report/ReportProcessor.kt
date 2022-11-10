package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.core.ParticipantStats
import com.leinb1dr.pubg.afteractionreport.core.SeasonStats
import com.leinb1dr.pubg.afteractionreport.match.RawReportStats
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class ReportProcessor {
    fun transformReport(rawReportStats: RawReportStats): Mono<Report> {
        return Mono.just(rawReportStats).map {
            val matchAttributes = it.matchStats.attributes!!
            val matchStats = it.matchStats.stats as ParticipantStats
            val seasonStats = it.seasonStats.stats as SeasonStats
            val roundsPlayed = seasonStats.roundsPlayed

            val fields = ReportFields(
                AnnotatedField(
                    matchStats.DBNOs,
                    setAnnotation(matchStats.DBNOs, seasonStats.DBNOs, roundsPlayed)
                ),
                AnnotatedField(
                    matchStats.assists,
                    setAnnotation(matchStats.assists, seasonStats.assists, roundsPlayed)
                ),
                AnnotatedField(
                    matchStats.damageDealt,
                    setAnnotation(matchStats.damageDealt, seasonStats.damageDealt, roundsPlayed)
                ),
                matchStats.deathType,
                AnnotatedField(
                    matchStats.headshotKills,
                    setAnnotation(matchStats.headshotKills, seasonStats.headshotKills, roundsPlayed)
                ),
                matchStats.name,
                AnnotatedField(
                    matchStats.kills,
                    setAnnotation(matchStats.kills, seasonStats.kills, roundsPlayed)
                ),
                matchStats.winPlace,
                isInteresting(matchStats.heals, seasonStats.heals)
            )

            return@map Report(
                getMapName(matchAttributes.mapName),
                formatMatchTime(matchAttributes.createdAt),
                matchStats.name,
                fields
            )
        }
    }

    private fun formatMatchTime(createdAt: OffsetDateTime): String {
        return createdAt
            .atZoneSameInstant(ZoneId.of("America/New_York"))
            .toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("MM/dd/YYYY hh:mm a"))
    }

    private fun getMapName(mapName: String): String {
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


    private fun isInteresting(matchStat: Int, seasonStat: Int): Int {
        val upper = seasonStat * 1.25
        val lower = seasonStat * .75
        if (matchStat >= upper || matchStat <= lower) return matchStat
        return -1
    }

    private fun setAnnotation(matchStat: Int, seasonStat: Int, roundsPlayed: Int): ReportAnnotation =
        if (matchStat == 0) ReportAnnotation.NONE else if ((seasonStat / roundsPlayed) >= seasonStat) ReportAnnotation.ABOVE else ReportAnnotation.BELOW

    private fun setAnnotation(matchStat: Double, seasonStat: Double, roundsPlayed: Int): ReportAnnotation =
        if (matchStat == 0.0) ReportAnnotation.NONE else if ((seasonStat / roundsPlayed) >= seasonStat) ReportAnnotation.ABOVE else ReportAnnotation.BELOW
}