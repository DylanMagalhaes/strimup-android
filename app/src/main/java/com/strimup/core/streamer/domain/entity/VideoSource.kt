package com.strimup.core.streamer.domain.entity

import java.net.URI

private val HOSTED_VIDEO_EXTENSIONS = setOf("mp4", "mov", "webm")
private val WEB_SCHEMES = setOf("http", "https")

sealed interface VideoSource {
    data class HostedFile(val url: String) : VideoSource

    data class ExternalLink(val url: String, val host: String, val platform: Platform) : VideoSource

    data object Unavailable : VideoSource

    enum class Platform {
        YOUTUBE,
        TIKTOK,
        TWITCH,
        INSTAGRAM,
        KICK,
        OTHER,
    }
}

fun Streamer.Video.source(): VideoSource {
    val trimmedUrl = url.trim()
    val uri = runCatching { URI(trimmedUrl) }.getOrNull()
    val host = uri?.host?.lowercase()?.removePrefix("www.")

    return when {
        uri == null || host.isNullOrEmpty() || uri.scheme?.lowercase() !in WEB_SCHEMES -> VideoSource.Unavailable
        uri.path.orEmpty().substringAfterLast('.', "").lowercase() in HOSTED_VIDEO_EXTENSIONS ->
            VideoSource.HostedFile(trimmedUrl)
        else -> VideoSource.ExternalLink(url = trimmedUrl, host = host, platform = host.toPlatform())
    }
}

private fun String.toPlatform(): VideoSource.Platform = when {
    isDomainOf("youtube.com") || isDomainOf("youtu.be") -> VideoSource.Platform.YOUTUBE
    isDomainOf("tiktok.com") -> VideoSource.Platform.TIKTOK
    isDomainOf("twitch.tv") -> VideoSource.Platform.TWITCH
    isDomainOf("instagram.com") -> VideoSource.Platform.INSTAGRAM
    isDomainOf("kick.com") -> VideoSource.Platform.KICK
    else -> VideoSource.Platform.OTHER
}

private fun String.isDomainOf(domain: String): Boolean = this == domain || endsWith(".$domain")
