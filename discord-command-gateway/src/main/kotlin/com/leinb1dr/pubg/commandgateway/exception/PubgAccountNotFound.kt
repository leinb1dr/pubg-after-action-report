package com.leinb1dr.pubg.commandgateway.exception

class PubgAccountNotFound(
    val username: String,
    override val message: String? = "",
    override val cause: Throwable? = null
): IllegalArgumentException() {
}