package com.strimup.feature.streamervideos.domain.usecase

import com.strimup.feature.streamervideos.domain.StreamerVideoRepository
import com.strimup.feature.streamervideos.domain.entity.InvalidVideoException
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.VideoPolicy
import javax.inject.Inject

class DefaultSelectVideoFileUseCase @Inject constructor(
    private val repository: StreamerVideoRepository,
) : SelectVideoFileUseCase {
    override suspend fun invoke(uri: String): Result<LocalVideoFile> {
        return repository.getLocalVideoFile(uri).mapCatching { file ->
            when {
                VideoPolicy.formatOf(file) == null ->
                    throw InvalidVideoException(InvalidVideoException.Reason.UNSUPPORTED_FORMAT)
                VideoPolicy.isTooLarge(file) ->
                    throw InvalidVideoException(InvalidVideoException.Reason.FILE_TOO_LARGE)
                else -> file
            }
        }
    }
}
