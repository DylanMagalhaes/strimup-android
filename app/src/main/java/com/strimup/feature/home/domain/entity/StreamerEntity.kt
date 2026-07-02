package com.strimup.feature.home.domain.entity

data class StreamerEntity(
    val id: String,
    val userName: String,
    val socials: List<Social>,
    val imageUrl: String,
    val isLive: Boolean,
    val liveTitle: String?,
    val isFavorite: Boolean,
    val tags: List<Tags>?,
) {
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
    data class Tags(
        val id: Int,
        val name: String
    )
}
