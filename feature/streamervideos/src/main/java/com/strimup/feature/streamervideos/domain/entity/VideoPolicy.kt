package com.strimup.feature.streamervideos.domain.entity

import java.net.URI

private const val BYTES_PER_MEGABYTE = 1024L * 1024L
private const val SCHEME_SEPARATOR = "://"
private const val DEFAULT_SCHEME = "https"
private val WEB_SCHEMES = setOf("http", "https")
private val SUPPORTED_LINK_DOMAINS = listOf("youtube.com", "youtu.be", "tiktok.com", "twitch.tv")

object VideoPolicy {
    const val MAX_VIDEOS = 3
    const val MAX_FILE_SIZE_MEGABYTES = 80L
    const val MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MEGABYTES * BYTES_PER_MEGABYTE
    const val MIN_TITLE_LENGTH = 2
    const val MAX_TITLE_LENGTH = 120
    const val MAX_DESCRIPTION_LENGTH = 1000
    const val MAX_LINK_LENGTH = 500

    fun formatOf(file: LocalVideoFile): VideoFormat? {
        val mimeType = file.mimeType?.lowercase()
        val extension = file.fileName?.substringAfterLast('.', missingDelimiterValue = "")?.lowercase()
        return VideoFormat.entries.firstOrNull { it.mimeType == mimeType }
            ?: VideoFormat.entries.firstOrNull { it.extension == extension }
    }

    fun isTooLarge(file: LocalVideoFile): Boolean = (file.sizeBytes ?: 0L) > MAX_FILE_SIZE_BYTES

    fun isValidTitle(title: String): Boolean = title.trim().length in MIN_TITLE_LENGTH..MAX_TITLE_LENGTH

    fun isValidDescription(description: String?): Boolean =
        description == null || description.trim().length <= MAX_DESCRIPTION_LENGTH

    fun normalizeLink(url: String): String {
        val trimmed = url.trim()
        val hasScheme = trimmed.isEmpty() || trimmed.contains(SCHEME_SEPARATOR)
        return if (hasScheme) trimmed else "$DEFAULT_SCHEME$SCHEME_SEPARATOR$trimmed"
    }

    fun isSupportedLink(url: String): Boolean {
        val normalized = normalizeLink(url).takeIf { it.isNotEmpty() && it.length <= MAX_LINK_LENGTH }
        val uri = normalized?.let { runCatching { URI(it) }.getOrNull() }
        val host = uri?.host?.lowercase()
        return host != null &&
            uri.scheme?.lowercase() in WEB_SCHEMES &&
            SUPPORTED_LINK_DOMAINS.any { domain -> host == domain || host.endsWith(".$domain") }
    }
}
