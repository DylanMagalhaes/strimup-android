package com.strimup.core.streamer.domain.entity

data class Social(
    val url: String?,
    val type: Type,
) {
    enum class Type {
        Twitch,
        Kick,
        Youtube,
        Instagram,
        Tiktok,
    }
}
