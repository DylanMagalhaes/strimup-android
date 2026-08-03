package com.strimup.common.domain.usecase

import com.strimup.common.data.repository.DefaultTagRepository
import com.strimup.common.domain.entity.TagEntity
import javax.inject.Inject

class GetTagsUsecase @Inject constructor(
    private val repository: DefaultTagRepository
) {
    suspend operator fun invoke(): Result<List<TagEntity>> {
        return repository.getTags()
    }
}