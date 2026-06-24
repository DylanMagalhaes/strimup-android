package com.strimup.feature.streamerdetail.domain.usecase

import com.strimup.feature.streamerdetail.domain.StreamerRepository
import com.strimup.feature.streamerdetail.domain.entity.StreamerDetailEntity
import javax.inject.Inject

class DefaultGetStreamerUseCase @Inject constructor(
    private val repository: StreamerRepository
) : GetStreamerUsecase {
    override suspend fun invoke(id: String): Result<StreamerDetailEntity> {
        return repository.getStreamerById(id)
    }
}