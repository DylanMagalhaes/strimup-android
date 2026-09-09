package com.strimup.core.tag.domain.usecase

import com.strimup.core.tag.domain.entity.TagEntity

fun interface GetTagsUseCase {
    suspend operator fun invoke(): Result<List<TagEntity>>
}
