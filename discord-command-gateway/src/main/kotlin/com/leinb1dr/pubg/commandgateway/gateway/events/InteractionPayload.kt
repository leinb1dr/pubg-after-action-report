package com.leinb1dr.pubg.commandgateway.gateway.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.time.OffsetDateTime

data class InteractionPayload(
    val id: Long,
    val applicationId: Long,
    val type: InteractionType,
    val data: InteractionData?,
    val guildId: Long?,
    val channelId: Long?,
    val member: GuildMember?,
    val user: DiscordUser?,
    val token: String,
    val version: Int,
    val message: DiscordMessage?,
    val appPermissions: String?,
    val locale: String?,
    val guildLocale: String?

)

data class DiscordMessage(
    val id: Long?=null,
    val channelId: Long?=null,
    val author: DiscordUser?=null,
    val content: String?=null,
//    val timestamp: OffsetDateTime,
//    val editedTimestamp: OffsetDateTime,
    val tts: Boolean?=null,
    val mentionEveryone: Boolean?=null,
    val mentions: Array<DiscordUser>?=null,
    val flags: Int?=null,
//    val mentionRoles: Array<DiscordRole>,
//    val channelMentions: Array<ChannelMention>,
//    val attachments: Array<DiscordAttachment>,
//    val embeds: Array<DiscordEmbeds>,
//    val reactions: Array<DiscordReactions>,
    val nonce: Any?=null,
    val pinned: Boolean?=null,
    val webhookId: Long?=null,
//    val type: MessageType,
//    val activity: DiscordMessageActivity,
)

data class GuildMember(
    val user: DiscordUser?,
    val nick: String?,
    val avatar: String?,
    val roles: Array<Long>,
    val joinedAt: OffsetDateTime?,
    val premiumSince: OffsetDateTime?,
    val deaf: Boolean,
    val mute: Boolean,
    val pending: Boolean?,
    val permissions: String,
//    val communicationDisabledUntil: OffsetDateTime
)

data class DiscordUser(
    val id: String,
    val username: String,
    val discriminator: String,
    val avatar: String?,
    val bot: Boolean?,
    val system: Boolean,
    val mfaEnabled: Boolean?,
    val banner: String?,
    val accentColor: Int?,
    val locale: String?,
    val verified: Boolean?,
    val email: String?,
    val flags: Int?,
    val premiumType: Int?,
    val publicFlags: Int?,
)

data class InteractionData(
    val id: Long, val name: String, val type: InteractionType,
//    val resolved: ResolvedData,
    val options: Array<InteractionOption>?, val guildId: Long?, val targetId: Long?
)

data class InteractionOption(
    val name: String, val type: CommandType, val value: Any?, val options: InteractionOption?, val focused: Boolean?
)

enum class CommandType(@get:JsonValue val value: Int) {
    SUB_COMMAND(1), SUB_COMMAND_GROUP(2), STRING(3), INTEGER(4), BOOLEAN(5), USER(6), CHANNEL(7), ROLE(8), MENTIONABLE(9), NUMBER(10),
    ATTACHMENT(11), INVALID(-1);

    companion object {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun fromLabel(interactionType: Int): CommandType =
            CommandType.values().firstOrNull { it.value == interactionType } ?: CommandType.INVALID
    }
}

//data class ResolvedData(
//    val users: Map<Long, DiscordUser>,
//    val members: Map<Long, PartialMember>,
//)

enum class InteractionType(@get:JsonValue val value: Int) {
    PING(1), APPLICATION_COMMAND(2), MESSAGE_COMPONENT(3), APPLICATION_COMMAND_AUTOCOMPLETE(4), MODAL_SUBMIT(5), INVALID(
        0
    );

    companion object {
        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        @JvmStatic
        fun fromLabel(interactionType: Int): InteractionType =
            InteractionType.values().firstOrNull { it.value == interactionType } ?: InteractionType.INVALID
    }
}
