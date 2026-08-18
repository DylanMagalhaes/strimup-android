package com.strimup.feature.filter.domain.entity

import com.strimup.common.domain.entity.StreamerEntity

data class StreamerMatchResult(
    val streamers: List<StreamerEntity>,
    val total: Int
)