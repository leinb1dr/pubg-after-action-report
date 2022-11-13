package com.leinb1dr.pubg.afteractionreport.message

import com.leinb1dr.pubg.afteractionreport.report.Report
import com.leinb1dr.pubg.afteractionreport.report.ReportFields
import reactor.core.publisher.Mono
import kotlin.reflect.full.memberProperties

fun Mono<Report>.reportToMessageTransformer(): Mono<DiscordMessage> {

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

        return@map DiscordMessage(
            mutableListOf(
                MessageEmbed(
                    "Pubg Match Report",
                    "Match on ${report.map} at ${report.time}",
                    fields

                )
            )
        )

    }
}