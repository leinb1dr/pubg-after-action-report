package com.leinb1dr.pubg.commandgateway.gateway.state

import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
data class WebsocketState(
    val sequence: AtomicInteger = AtomicInteger(0),
    val heartbeatInterval: AtomicInteger = AtomicInteger(1000),
    val shards: AtomicInteger = AtomicInteger(1)
)