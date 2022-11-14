package com.leinb1dr.pubg.afteractionreport.message

import com.leinb1dr.pubg.afteractionreport.report.Report
import com.leinb1dr.pubg.afteractionreport.report.ReportFields
import com.leinb1dr.pubg.afteractionreport.report.TeamReport
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.reflect.full.memberProperties

val labels: Map<String, String> = mapOf(
//    Pair("name", "Name"),
    Pair("deathType", "Death"),
//    Pair("winPlace", "Place"),
    Pair("kills", "Kills"),
    Pair("headshotKills", "Headshot Kills"),
    Pair("killPlace", "Kill Place"),
    Pair("assists", "Assists"),
    Pair("DBNOs", "Knocks"),
    Pair("damageDealt", "Damage Dealt"),
    Pair("heals", "Heals Used"),
    Pair("revives", "Revives"),
    Pair("killStreaks", "Kill Streaks"),
    Pair("longestKill", "Longest Kill"),
    Pair("rideDistance", "Ride Distance"),
    Pair("roadKills", "Road Kills"),
    Pair("swimDistance", "swimDistance"),
    Pair("teamKills", "Team Kills"),
    Pair("timeSurvived", "Time Survived"),
    Pair("vehicleDestroys", "Vehicles Destroyed"),
    Pair("walkDistance", "Walk Distance"),
    Pair("weaponsAcquired", "Weapons Acquired")
)

val order: List<String> = labels.entries.map { it.value }

fun Flux<Report>.reportToMessageFieldTransformer(): Flux<MessageFields> {
    return this.map { report ->
        val playerStats = mutableListOf<Pair<String, String>>()
        val reportFieldProperties = ReportFields::class.memberProperties
        for (property in reportFieldProperties) {
            labels[property.name]?.apply {
                when (val statVal = property.get(report.fields)) {
                    is Int -> if (statVal >= 0) playerStats.add(Pair("*$this*", "$statVal"))
                    is Double -> if (statVal >= 0.0) playerStats.add(Pair("*$this*", String.format("%.2f", statVal)))
                    else -> playerStats.add(Pair("*$this*", "$statVal"))
                }
            }
        }

        MessageFields(report.playerName,
            ">>> ${
                playerStats.stream().sorted { o1, o2 -> order.indexOf(o1.first).compareTo(order.indexOf(o2.first)) }
                    .map { "${it.first}: ${it.second}" }
                    .toArray().joinToString("\n")
            }"
        )
    }
}

fun Mono<List<MessageFields>>.mapToDiscordMessage(report: Report): Mono<DiscordMessage> {
    return this.map {

        val timeSurvived = report.fields.timeSurvived

        DiscordMessage(
            listOf(
                MessageEmbed(
                    "${report.map.label} Match Results",
                    "The team placed ${placementFormat(report.fields.winPlace)} and survived for ${
                        String.format("%.2f", timeSurvived)
                    } minutes",
                    it.toMutableList()
                )
            )
        )
    }
}

fun Mono<List<MessageFields>>.mapToDiscordMessage(teamReport: TeamReport): Mono<DiscordMessage> {
    return this.map {

        val timeSurvived =
            teamReport.reports.stream().map { it.fields.timeSurvived }.max { o1, o2 -> o1.compareTo(o2) }.get()

        DiscordMessage(
            listOf(
                MessageEmbed(
                    "After Action Report for ${teamReport.matchAttributes.mapName.label}",
                    "The team placed ${placementFormat(teamReport.place)} and survived for ${
                        String.format( "%.2f", timeSurvived )
                    } minutes",
                    it.toMutableList()
                )
            )
        )
    }
}

fun placementFormat(place: Int): String =
    when {
        place % 10 == 1 -> "${place}st"
        place % 10 == 2 -> "${place}nd"
        place % 10 == 3 -> "${place}rd"
        else -> "${place}th"

    }

