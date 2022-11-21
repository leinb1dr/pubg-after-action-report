package com.leinb1dr.pubg.commandgateway.gateway.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class OpCode(@get:JsonValue val code: Int) {
    DISPATCH(0), HEARTBEAT(1), IDENTIFY(2), INVALID_SESSION(9), HELLO(10), HEARTBEAT_ACK(11), NONE(-1);

    companion object {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun fromLabel(opCode: Int): OpCode =
            values().firstOrNull { it.code == opCode } ?: NONE
    }

}
