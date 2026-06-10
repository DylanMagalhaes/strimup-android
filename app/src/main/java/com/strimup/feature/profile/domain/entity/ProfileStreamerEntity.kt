package com.strimup.feature.profile.domain.entity

data class ProfileStreamerEntity(
    val userName: String,
    val socials: List<Social>,
    val imageUrl: String,
    val isLive: Boolean,
    val bio: String,
    val tags: List<Tag>,
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
}
