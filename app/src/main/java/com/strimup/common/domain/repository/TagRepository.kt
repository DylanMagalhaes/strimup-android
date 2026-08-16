package com.strimup.common.domain.repository

import com.strimup.common.domain.entity.TagEntity

interface TagRepository {
    suspend fun getTags(): Result<List<TagEntity>>
}