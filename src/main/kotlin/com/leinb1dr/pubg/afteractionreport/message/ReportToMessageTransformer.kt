package com.leinb1dr.pubg.afteractionreport.message

import com.leinb1dr.pubg.afteractionreport.report.Report
import com.leinb1dr.pubg.afteractionreport.report.ReportFields
import reactor.core.publisher.Mono
import kotlin.reflect.full.memberProperties

private val labels: Map<String, String> = mapOf(
    Pair("deathType", "Death"),
    Pair("winPlace", "Place"),
    Pair("kills", "Kills"),
    Pair("headshotKills", "Headshot Kills"),
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

fun Mono<Report>.reportToMessageTransformer(): Mono<DiscordMessage> {

    return this.map { report ->
//        val fields = mutableListOf<MessageFields>()
        val playerStats = mutableListOf<Pair<String, String>>()
        val reportFieldProperties = ReportFields::class.memberProperties
        for (property in reportFieldProperties) {
            labels[property.name]?.apply {
                when (val statVal = property.get(report.fields)) {
                    is Number -> if (statVal.toInt() >= 0) playerStats.add(Pair("***$this***", "$statVal"))
                    else -> playerStats.add(Pair("***$this***", "$statVal"))
                }
            }
        }

        playerStats.sortWith { o1, o2 -> order.indexOf(o1.first).compareTo(order.indexOf(o2.first)) }
        val playerStatsString = playerStats.map { "${it.first}: ${it.second}" }

        val fields = mutableListOf<MessageFields>()
        fields.add(
            MessageFields(
                report.playerName,
                playerStatsString.joinToString("\n"))
        )

        return@map DiscordMessage(
            arrayOf(
                MessageEmbed(
                    "Pubg Match Report",
                    "Match on ${report.map} at ${report.time}",
                    fields

                )
            )
        )

    }
}