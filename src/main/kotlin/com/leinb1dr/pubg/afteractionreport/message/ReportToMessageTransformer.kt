package com.leinb1dr.pubg.afteractionreport.message

import com.leinb1dr.pubg.afteractionreport.report.Report
import com.leinb1dr.pubg.afteractionreport.report.ReportAnnotation
import com.leinb1dr.pubg.afteractionreport.report.ReportFields
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.reflect.full.memberProperties

private val labels: Map<String, String> = mapOf(
    Pair("name", "Name"),
    Pair("deathType", "Death"),
    Pair("winPlace", "Place"),
    Pair("kills", "Kills"),
    Pair("headshotKills", "Headshot Kills"),
    Pair("assists", "Assists"),
    Pair("DBNOs", "Knocks"),
    Pair("damageDealt", "Damage Dealt"),
    Pair("heals", "Heals Used")
)

val order: List<String> = labels.entries.map { it.value }

fun Mono<Report>.reportToMessageTransformer(): Mono<DiscordMessage> {

    return this.map { report ->
        val fields = mutableListOf<MessageFields>()
        val reportFieldProperties = ReportFields::class.memberProperties
        for (property in reportFieldProperties) {
            val annotation = reportFieldProperties.firstOrNull { it.name == "${property.name}Annotation" }
            val statName = labels[property.name]
            val statVal = property.get(report.fields)
            if ((statVal is Number && statVal.toInt() >= 0) || statVal is String)
                fields.add(
                    MessageFields(statName!!, ("$statVal ${
                        (annotation?.get(report.fields)?.let { (it as ReportAnnotation).emoji } ?: "")
                    }".trim()))
                )
        }

        fields.sortWith { o1, o2 -> order.indexOf(o1.name).compareTo(order.indexOf(o2.name)) }

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