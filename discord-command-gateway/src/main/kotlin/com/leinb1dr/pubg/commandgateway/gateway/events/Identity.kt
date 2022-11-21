package com.leinb1dr.pubg.commandgateway.gateway.events

data class Identity(override val d: IdentityPayload, override val s: Int? = null, override val t: String? = null) :
    DiscordEvent(OpCode.IDENTIFY, d, s, t)

data class IdentityPayload(
    val token: String,
    val intents: Int,
    val shards: Array<Int>,
    val properties: ConnectionProperties
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IdentityPayload

        if (token != other.token) return false
        if (intents != other.intents) return false
        if (!shards.contentEquals(other.shards)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = token.hashCode()
        result = 31 * result + intents
        result = 31 * result + shards.contentHashCode()
        return result
    }
}

data class ConnectionProperties(val os: String, val browser:String) {
    val device = browser

}
