package com.leinb1dr.pubg.afteractionreport.report

import com.leinb1dr.pubg.afteractionreport.core.ParticipantStats
import com.leinb1dr.pubg.afteractionreport.core.SeasonStats
import com.leinb1dr.pubg.afteractionreport.match.RawReportStats
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.max

@Service
class ReportProcessor {
    fun transformReport(rawReportStats: RawReportStats): Mono<Report> {
        return Mono.just(rawReportStats).map {
            val matchAttributes = it.matchStats.attributes!!
            val matchStats = it.matchStats.stats as ParticipantStats
            val seasonStats = it.seasonStats.stats as SeasonStats
            // Ensure no div by 0 error
            val roundsPlayed = max(1, seasonStats.roundsPlayed)

            val fields = ReportFields(
                AnnotatedField(
                    matchStats.DBNOs,
                    setAnnotation(matchStats.DBNOs, seasonStats.DBNOs / roundsPlayed)
                ),
                AnnotatedField(
                    matchStats.assists,
                    setAnnotation(matchStats.assists, seasonStats.assists / roundsPlayed)
                ),
                AnnotatedField(
                    matchStats.damageDealt,
                    setAnnotation(matchStats.damageDealt, seasonStats.damageDealt / roundsPlayed)
                ),
                matchStats.deathType,
                AnnotatedField(
                    matchStats.headshotKills,
                    setAnnotation(matchStats.headshotKills, seasonStats.headshotKills / roundsPlayed)
                ),
                matchStats.name,
                AnnotatedField(
                    matchStats.kills,
                    setAnnotation(matchStats.kills, seasonStats.kills / roundsPlayed)
                ),
                matchStats.winPlace,
                isInteresting(matchStats.heals, seasonStats.heals),
                isInteresting(matchStats.revives, seasonStats.revives),
                isInteresting(matchStats.killStreaks, seasonStats.maxKillStreaks),
                isInteresting(matchStats.longestKill, seasonStats.longestKill),
                isInteresting(matchStats.rideDistance, seasonStats.rideDistance),
                isInteresting(matchStats.roadKills, seasonStats.roadKills),
                isInteresting(matchStats.swimDistance, seasonStats.swimDistance),
                isInteresting(matchStats.teamKills, seasonStats.teamKills),
                isInteresting(matchStats.timeSurvived, seasonStats.timeSurvived),
                isInteresting(matchStats.vehicleDestroys, seasonStats.vehicleDestroys),
                isInteresting(matchStats.walkDistance, seasonStats.walkDistance),
                isInteresting(matchStats.weaponsAcquired, seasonStats.weaponsAcquired):
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
        if (matchStat >= upper) return matchStat
        return -1
    }

    private fun isInteresting(matchStat: Double, seasonStat: Double): Double {
        val upper = seasonStat * 1.25
        if (matchStat >= upper) return matchStat
        return -1.0
    }

    private val matchCompare: Comparator<Number> = Comparator { match, season ->
        when (match) {
            is Double -> match.compareTo(season as Double)
            is Int -> match.compareTo(season as Int)
            else -> 0
        }
    }

    private fun <T : Number> setAnnotation(matchStat: T, seasonAverage: T): ReportAnnotation =
        when (matchCompare.compare(matchStat, seasonAverage)) {
            -1 -> ReportAnnotation.BELOW
            0 -> ReportAnnotation.EVEN
            1 -> ReportAnnotation.ABOVE
            else -> ReportAnnotation.NONE
        }


//        if (seasonStat == 0.0) ReportAnnotation.NONE else if ((seasonStat / roundsPlayed) > matchStat) ReportAnnotation.BELOW else ReportAnnotation.ABOVE
}