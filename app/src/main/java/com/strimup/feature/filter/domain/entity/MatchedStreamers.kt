package com.strimup.feature.filter.domain.entity

data class MatchedStreamerEntity(
    val id: Int,
    val username: String,
    val imageUrl: String?,
    val isVerified: Boolean,
    val isLive: Boolean
)

data class StreamerMatchResult(
    val streamers: List<MatchedStreamerEntity>,
    val page: Int,
    val total: Int
)