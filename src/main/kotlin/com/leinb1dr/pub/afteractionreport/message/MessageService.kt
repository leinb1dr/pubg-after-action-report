package com.leinb1dr.pub.afteractionreport.message

import com.fasterxml.jackson.databind.ObjectMapper
import com.leinb1dr.pub.afteractionreport.report.Report
import com.leinb1dr.pub.afteractionreport.report.ReportAnnotation
import com.leinb1dr.pub.afteractionreport.report.ReportFields
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import kotlin.reflect.full.memberProperties

@Service
class MessageService(
    @Autowired @Qualifier("discordClient") val client: WebClient,
    @Autowired val mapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(MessageService::class.java)

    val labels: Map<String, String> = mapOf(
        Pair("name", "Name"),
        Pair("deathType", "Death"),
        Pair("winPlace", "Place"),
        Pair("kills", "Kills"),
        Pair("headshotKills", "Headshot Kills"),
        Pair("assists", "Assists"),
        Pair("DBNOs", "Knocks"),
        Pair("damageDealt", "Damage Dealt")
    )

    val order: Array<String> = arrayOf(
        "Name",
        "Death",
        "Place",
        "Kills",
        "Headshot Kills",
        "Assists",
        "Knocks",
        "Damage Dealt"
    )

    fun postMessage(stats: Report): Mono<Boolean> {

        val fields = mutableListOf<MutableMap<String, Any?>>()
        val reportFieldProperties = ReportFields::class.memberProperties
        for (property in reportFieldProperties) {
            if(!property.name.endsWith("Annotation")) {
                val annotation = reportFieldProperties.filter { it.name == "${property.name}Annotation" }.firstOrNull()
                fields.add(
                    mutableMapOf(
                        Pair("name", labels[property.name]),
                        Pair("value", "${property.get(stats.fields)} ${(annotation?.get(stats.fields)?.let { (it as ReportAnnotation).emoji } )}"),
                        Pair("inline", "true")
                    )
                )
            }
        }

        fields.sortWith { o1, o2 -> order.indexOf(o1["name"]).compareTo(order.indexOf(o2["name"])) }

        val message = mapOf(
            Pair(
                "embeds",
                arrayOf(
                    mapOf(
                        Pair("title", "Pubg Match Report"),
                        Pair("description", "Match on ${stats.map} at ${stats.time}"),
                        Pair("fields", fields)
                    )
                )
            )
        )

        println(mapper.writeValueAsString(message))

        return client.post().bodyValue(message).exchangeToMono {
            Mono.just(it.statusCode().is2xxSuccessful)
                .doOnError { t -> logger.error("Failed to send message to discord", t) }
                .onErrorResume { Mono.empty() }
        }
    }
}