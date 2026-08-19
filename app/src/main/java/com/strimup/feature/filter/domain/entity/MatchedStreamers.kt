package com.strimup.feature.filter.domain.entity

import com.strimup.core.streamer.domain.entity.StreamerEntity

data class StreamerMatchResult(
    val streamers: List<StreamerEntity>,
    val total: Int
)