package com.strimup.feature.profile.domain.usecase

import com.strimup.feature.profile.domain.StreamerRepository
import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity
import javax.inject.Inject

class DefaultGetStreamerUseCase @Inject constructor(
    private val repository: StreamerRepository
) : GetStreamerUsecase {
    override suspend fun invoke(id: String): Result<ProfileStreamerEntity> {
        return repository.getStreamerById(id)
    }
}