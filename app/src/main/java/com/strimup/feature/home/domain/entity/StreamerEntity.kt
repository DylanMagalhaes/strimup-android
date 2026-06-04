package com.strimup.feature.home.domain.entity

data class StreamerEntity(
    val userName: String,
    val socials: List<Social>,
    val imageUrl: String,
) {
    data class Social(
        val url: String,
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
}
