package com.strimup.feature.streamervideos.domain.usecase

import com.strimup.feature.streamervideos.domain.StreamerVideoRepository
import javax.inject.Inject

class DefaultDeleteVideoUseCase @Inject constructor(
    private val repository: StreamerVideoRepository,
) : DeleteVideoUseCase {
    override suspend fun invoke(id: String): Result<Unit> = repository.deleteVideo(id)
}
