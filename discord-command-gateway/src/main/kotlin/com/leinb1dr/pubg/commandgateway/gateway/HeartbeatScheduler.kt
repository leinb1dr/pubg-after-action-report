package com.leinb1dr.pubg.commandgateway.gateway

import com.leinb1dr.pubg.commandgateway.gateway.events.DiscordEvent
import com.leinb1dr.pubg.commandgateway.gateway.events.HeartBeat
import com.leinb1dr.pubg.commandgateway.gateway.state.WebsocketState
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.annotation.PostConstruct

@Service
class HeartbeatScheduler(
    @Autowired @Qualifier("websocket") private val websocketSink: Sinks.Many<DiscordEvent>,
    @Autowired @Qualifier("heartbeat") private val heartbeat: Sinks.Many<Long>,
    @Autowired private val websocketState: WebsocketState,
) {

    val logger = LoggerFactory.getLogger(HeartbeatScheduler::class.java)!!

    @PostConstruct
    fun scheduleHeartbeat() {


        heartbeat.asFlux()
            .publish()
            .autoConnect()
            .flatMap {
                Flux.just(it)
                    .delayElements(run {
                        val nextHeartbeat = (websocketState.heartbeatInterval.get() * Math.random()).toLong()
                        logger.info(
                            "Next heartbeat should be sent at: ${
                                LocalDateTime.now().plus(nextHeartbeat, ChronoUnit.MILLIS)
                            }"
                        )
                        Duration.ofMillis(nextHeartbeat)
                    })
            }
            .doOnNext { logger.info("Heartbeat triggered") }
            .subscribe { websocketSink.tryEmitNext(HeartBeat(1)) }
    }
}
