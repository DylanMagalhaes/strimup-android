package com.strimup.feature.streamerprofile.domain.usecase

import com.strimup.feature.streamerprofile.domain.StreamerRepository
import com.strimup.feature.streamerprofile.domain.entity.StreamerOptionsEntity
import javax.inject.Inject

class GetStreamerOptionsUseCase @Inject constructor(
    private val repository: StreamerRepository
) {
    suspend operator fun invoke(): Result<StreamerOptionsEntity> {
        return repository.getStreamerOptions()
    }
}