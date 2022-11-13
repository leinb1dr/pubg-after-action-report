package com.leinb1dr.pubg.afteractionreport.message

import com.leinb1dr.pubg.afteractionreport.report.Report
import com.leinb1dr.pubg.afteractionreport.report.ReportFields
import reactor.core.publisher.Mono
import kotlin.reflect.full.memberProperties

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