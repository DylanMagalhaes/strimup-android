package com.strimup.feature.filter.data.mapper

import com.strimup.feature.filter.data.response.MatchedStreamerDto
import com.strimup.feature.filter.domain.entity.MatchedStreamerEntity

fun MatchedStreamerDto.toDomain(): MatchedStreamerEntity {
    return MatchedStreamerEntity(
        id = id,
        username = username,
        imageUrl = imageUrl,
        isVerified = isVerified,
        isLive = isLive
    )
}