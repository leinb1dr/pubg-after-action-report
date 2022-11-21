package com.leinb1dr.pubg.commandgateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class AfterActionReportApplication

fun main(args: Array<String>) {
    runApplication<AfterActionReportApplication>(*args)

//    val botToken = "Mzk5MTAyMTM5NTE3MTA4MjQ4.GQR44n.CrpESWHshoy95JXlvep4-IgOTGqOcjBNU5GVd4"
//
//    val webSocketClient = StandardWebSocketClient()
//
//    WebClient.create().get().uri("https://discord.com/api/gateway/bot")
//        .headers { it["Authorization"] = "Bot $botToken" }
//        .retrieve()
//        .bodyToMono<Map<String, Any>>()
//        .log()
//        .flatMap { gatewayResponse ->
//            webSocketClient.execute(URI.create(gatewayResponse["url"] as String)){
//                it.receive()
//                    .flatMap {
//
//                    }
//            }
//        }
//        .block(Duration.ofSeconds(10))

//    client.execute(URI.create("ws://gateway.discord.gg/")) { session ->
//        session.handshakeInfo
//        session.send(
//            Mono.just(
//                session.textMessage(
//                    "{\n" +
//                            "  \"op\": 2,\n" +
//                            "  \"d\": {\n" +
//                            "    \"token\": \"Mzk5MTAyMTM5NTE3MTA4MjQ4.GQR44n.CrpESWHshoy95JXlvep4-IgOTGqOcjBNU5GVd4\",\n" +
//                            "    \"intents\": 256,\n" +
//                            "    \"properties\": {\n" +
//                            "      \"os\": \"linux\",\n" +
//                            "      \"browser\": \"disco\",\n" +
//                            "      \"device\": \"disco\"\n" +
//                            "    }\n" +
//                            "  }\n" +
//                            "}"
//                )
//            )
//        )
//    }.block(Duration.ofSeconds(10L))
//        session.handshakeInfo

//        ).flatMapMany {
//            session.receive()
//                .map(WebSocketMessage::getPayloadAsText)
//                .log()
//        }
//            .then()
//    }
//        .block(Duration.ofSeconds(10L))
}
