package com.strimup.core.streamer.domain.entity

data class VideoEmbed(
    val provider: Provider,
    val id: String,
    val kind: Kind,
    val orientation: VideoOrientation,
) {
    enum class Provider {
        YOUTUBE,
        TIKTOK,
        TWITCH,
    }

    enum class Kind {
        VIDEO,
        SHORT,
        VOD,
        CLIP,
    }
}
