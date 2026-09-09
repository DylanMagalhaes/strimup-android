package com.strimup.core.tag.domain.usecase

import com.strimup.core.tag.data.repository.DefaultTagRepository
import com.strimup.core.tag.domain.entity.TagEntity
import javax.inject.Inject

class DefaultGetTagsUseCase @Inject constructor(
    private val repository: DefaultTagRepository
) : GetTagsUseCase {
    override suspend fun invoke(): Result<List<TagEntity>> {
        return repository.getTags()
    }
}
