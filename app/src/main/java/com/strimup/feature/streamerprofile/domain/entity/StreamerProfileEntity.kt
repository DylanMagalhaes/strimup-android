package com.strimup.feature.streamerprofile.domain.entity

data class StreamerProfileEntity(
    val userName: String,
    val imageUrl: String,
    val isLive: Boolean,
    val bio: String?,
    val dailyStatus: String?,
    val socials: List<Social>,
    val tags: List<Tag>?,
    val videos: List<Video>?,
    val averageViewers: Int?,
    val languages: List<String>?,
    val personality: String?,
    val personalitySecondary: String?,
    val streamFrequency: String?
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