package com.strimup.core.tag.data.repository

import com.strimup.core.tag.data.remote.TagApiService
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.core.tag.domain.repository.TagRepository
import com.strimup.feature.streamerprofile.data.mapper.toEntity
import javax.inject.Inject

class DefaultTagRepository @Inject constructor(
    private val service: TagApiService
): TagRepository {
    override suspend fun getTags(): Result<List<TagEntity>> {
        return runCatching {
            service.getTags().map {
                it.toEntity()
            }
        }
    }
}