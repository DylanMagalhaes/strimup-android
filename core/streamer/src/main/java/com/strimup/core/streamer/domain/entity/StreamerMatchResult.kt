package com.strimup.core.streamer.domain.entity

data class StreamerMatchResult(
    val streamers: List<Streamer>,
    val total: Int
)
