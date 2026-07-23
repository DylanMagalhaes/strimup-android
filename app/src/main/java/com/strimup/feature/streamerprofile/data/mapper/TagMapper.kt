package com.strimup.feature.streamerprofile.data.mapper

import com.strimup.feature.streamerprofile.data.response.TagResponse
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity

fun TagResponse.toEntity(): StreamerProfileEntity.Tag {
    return StreamerProfileEntity.Tag(
        id = this.id,
        name = this.name,
        category = this.category
    )
}