package com.strimup.core.streamer.domain.entity

const val EMBED_PARENT_DOMAIN = "strimup.com"

fun VideoEmbed.playerUrl(autoplay: Boolean, parentDomain: String = EMBED_PARENT_DOMAIN): String {
    val autoplayFlag = if (autoplay) "1" else "0"
    return when (provider) {
        VideoEmbed.Provider.YOUTUBE ->
            "https://www.youtube-nocookie.com/embed/$id?autoplay=$autoplayFlag&rel=0&playsinline=1"
        VideoEmbed.Provider.TIKTOK ->
            "https://www.tiktok.com/player/v1/$id?autoplay=$autoplayFlag&rel=0"
        VideoEmbed.Provider.TWITCH -> if (kind == VideoEmbed.Kind.CLIP) {
            "https://clips.twitch.tv/embed?clip=$id&parent=$parentDomain&autoplay=$autoplay"
        } else {
            "https://player.twitch.tv/?video=$id&parent=$parentDomain&autoplay=$autoplay"
        }
    }
}

fun VideoEmbed.thumbnailUrl(videoId: String, apiBaseUrl: String): String? = when (provider) {
    VideoEmbed.Provider.YOUTUBE -> "https://i.ytimg.com/vi/$id/hqdefault.jpg"
    VideoEmbed.Provider.TWITCH -> "${apiBaseUrl.trimEnd('/')}/api/streamer/videos/$videoId/thumbnail"
    VideoEmbed.Provider.TIKTOK -> null
}
