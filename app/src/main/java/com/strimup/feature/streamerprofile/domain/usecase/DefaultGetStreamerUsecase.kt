package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.feature.streamerprofile.domain.StreamerRepository
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity
import javax.inject.Inject

class DefaultGetStreamerUsecase @Inject constructor(
    private val repository: StreamerRepository
) : GetStreamerUsecase {
    override suspend fun invoke(id: String): Result<StreamerProfileEntity> {
        return repository.getStreamerById(id)
    }
}