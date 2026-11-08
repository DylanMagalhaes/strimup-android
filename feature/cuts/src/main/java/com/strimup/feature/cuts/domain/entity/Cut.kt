package com.strimup.feature.cuts.domain.entity

import com.strimup.core.streamer.domain.entity.Streamer

data class Cut(
    val video: Streamer.Video,
    val author: Author,
    val likesCount: Int,
    val isLikedByMe: Boolean,
) {
    val id: String get() = video.id

    data class Author(
        val id: String,
        val username: String,
        val avatarUrl: String,
        val isVerified: Boolean,
        val isLive: Boolean,
    )
}
