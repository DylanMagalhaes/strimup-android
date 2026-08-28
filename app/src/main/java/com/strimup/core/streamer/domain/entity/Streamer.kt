package com.strimup.core.streamer.domain.entity

import com.strimup.core.tag.domain.entity.TagEntity

data class Streamer(
    val id: String,
    val userName: String,
    val imageUrl: String?,
    val socials: List<Social> = emptyList(),
    val isLive: Boolean = false,
    val liveTitle: String? = null,
    val isFavorite: Boolean? = null,
    val bio: String? = null,
    val dailyStatus: String? = null,
    val followersCount: Int? = null,
    val tags: List<TagEntity>? = null,
    val videos: List<Video> = emptyList(),
    val averageViewers: String? = null,
    val languages: List<String>? = null,
    val personality: String? = null,
    val personalitySecondary: String? = null,
    val streamFrequency: String? = null,
) {
    data class Video(
        val id: String,
        val title: String,
        val description: String,
        val url: String,
        val order: Int
    )
}
