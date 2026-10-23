package com.strimup.core.streamer.data.mapper

import com.strimup.core.network.BuildConfig
import com.strimup.core.streamer.data.response.StreamerDto
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.VideoEmbed
import com.strimup.core.streamer.domain.entity.VideoOrientation
import com.strimup.core.streamer.domain.entity.thumbnailUrl

fun StreamerDto.Video.toEntity(apiBaseUrl: String = BuildConfig.BASE_URL): Streamer.Video {
    val videoEmbed = embed?.toEntity()
    return Streamer.Video(
        id = id,
        title = title,
        description = description.orEmpty(),
        url = url,
        order = order,
        embed = videoEmbed,
        thumbnailUrl = videoEmbed?.thumbnailUrl(videoId = id, apiBaseUrl = apiBaseUrl),
    )
}

internal fun StreamerDto.Embed.toEntity(): VideoEmbed? {
    val videoProvider = when (provider.lowercase()) {
        "youtube" -> VideoEmbed.Provider.YOUTUBE
        "tiktok" -> VideoEmbed.Provider.TIKTOK
        "twitch" -> VideoEmbed.Provider.TWITCH
        else -> null
    }
    if (videoProvider == null || id.isBlank()) return null

    return VideoEmbed(
        provider = videoProvider,
        id = id,
        kind = when (kind?.lowercase()) {
            "short" -> VideoEmbed.Kind.SHORT
            "vod" -> VideoEmbed.Kind.VOD
            "clip" -> VideoEmbed.Kind.CLIP
            else -> VideoEmbed.Kind.VIDEO
        },
        orientation = when (orientation?.lowercase()) {
            "vertical" -> VideoOrientation.VERTICAL
            else -> VideoOrientation.HORIZONTAL
        },
    )
}
