package com.strimup.core.tag.data.mapper

import com.strimup.core.tag.data.response.TagResponse
import com.strimup.core.tag.domain.entity.TagEntity

fun TagResponse.toEntity(): TagEntity {
    return TagEntity(
        id = this.id,
        name = this.name,
        category = this.category
    )
}
