package com.leinb1dr.pubg.afteractionreport.message

import com.leinb1dr.pubg.afteractionreport.report.Report
import com.leinb1dr.pubg.afteractionreport.report.ReportFields
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.reflect.full.memberProperties

val labels: Map<String, String> = mapOf(
    Pair("name", "Name"),
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

fun Flux<Report>.reportToMessageEmbedTransformer(): Flux<MessageEmbed> {
    return this.map { report ->
        val fields = mutableListOf<MessageFields>()
        val reportFieldProperties = ReportFields::class.memberProperties
        for (property in reportFieldProperties) {
            val statName: String = labels[property.name] ?: "Missing label"

            when (val statVal = property.get(report.fields)) {
                is Number -> if (statVal.toInt() >= 0) fields.add(MessageFields(statName, ("$statVal")))
                else -> fields.add(MessageFields(statName, ("$statVal")))
            }
        }

        fields.sortWith { o1, o2 -> order.indexOf(o1.name).compareTo(order.indexOf(o2.name)) }

        MessageEmbed(
            report.playerName,
            "",
            fields
        )

    }
}

fun Mono<List<MessageEmbed>>.mapToDiscordMessage():Mono<DiscordMessage> {
    return this.map { DiscordMessage(it) }
}