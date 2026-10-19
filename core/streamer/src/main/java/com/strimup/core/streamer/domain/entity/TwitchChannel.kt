package com.strimup.core.streamer.domain.entity

import java.net.URI

private val TWITCH_LOGIN = Regex("^[a-zA-Z0-9_]{3,25}$")
private val TWITCH_RESERVED_PATHS = setOf("directory", "videos", "search", "settings", "p", "downloads", "jobs")

fun Streamer.twitchChannel(): String? =
    socials.firstOrNull { it.type == Social.Type.Twitch }?.url?.toTwitchChannel()

internal fun String.toTwitchChannel(): String? {
    val trimmed = trim()
    val withScheme = if (trimmed.contains("://")) trimmed else "https://$trimmed"
    val uri = runCatching { URI(withScheme) }.getOrNull()
    val host = uri?.host?.lowercase()

    return uri?.path
        ?.takeIf { host != null && host.isTwitchChannelHost() }
        ?.split('/')
        ?.firstOrNull { it.isNotEmpty() }
        ?.removePrefix("@")
        ?.lowercase()
        ?.takeIf { TWITCH_LOGIN.matches(it) && it !in TWITCH_RESERVED_PATHS }
}

private fun String.isTwitchChannelHost(): Boolean =
    (this == "twitch.tv" || endsWith(".twitch.tv")) && !startsWith("clips.")
