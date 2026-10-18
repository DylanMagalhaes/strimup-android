package com.strimup.core.tag.domain.repository

import com.strimup.core.tag.domain.entity.TagEntity

interface TagRepository {
    suspend fun getTags(): Result<List<TagEntity>>
}