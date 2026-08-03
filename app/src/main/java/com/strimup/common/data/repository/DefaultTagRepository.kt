package com.strimup.common.data.repository

import com.strimup.common.data.remote.TagApiService
import com.strimup.common.domain.entity.TagEntity
import com.strimup.common.domain.repository.TagRepository
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