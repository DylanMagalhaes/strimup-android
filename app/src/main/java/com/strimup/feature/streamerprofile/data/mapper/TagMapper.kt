package com.strimup.feature.streamerprofile.data.mapper

import com.strimup.feature.streamerprofile.data.response.TagResponse
import com.strimup.core.tag.domain.entity.TagEntity

fun TagResponse.toEntity(): TagEntity {
    return TagEntity(
        id = this.id,
        name = this.name,
        category = this.category
    )
}