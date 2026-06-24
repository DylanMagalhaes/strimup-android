package com.strimup.feature.streamerdetail.domain.entity

data class StreamerDetailEntity(
    val userName: String,
    val socials: List<Social>,
    val imageUrl: String,
    val isLive: Boolean,
    val bio: String,
    val dailyStatus: String,
    val tags: List<Tag>?,
    val videos: List<Video>?
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

    data class Tag(
        val name: String,
        val category: String
    )

    data class Video(
        val title: String,
        val description: String,
        val url: String,
        val order: Int
    )
}
