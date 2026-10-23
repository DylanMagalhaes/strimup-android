package com.strimup.feature.streamervideos.domain.usecase

import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.streamervideos.domain.StreamerVideoRepository
import com.strimup.feature.streamervideos.domain.entity.InvalidVideoException
import com.strimup.feature.streamervideos.domain.entity.NewStreamerVideoLink
import com.strimup.feature.streamervideos.domain.entity.VideoPolicy
import javax.inject.Inject

class DefaultAddVideoLinkUseCase @Inject constructor(
    private val repository: StreamerVideoRepository,
) : AddVideoLinkUseCase {
    override suspend fun invoke(url: String, title: String, description: String): Result<Streamer.Video> {
        val trimmedDescription = description.trim().takeIf { it.isNotEmpty() }
        val invalidReason = when {
            !VideoPolicy.isSupportedLink(url) -> InvalidVideoException.Reason.INVALID_LINK
            !VideoPolicy.isValidTitle(title) -> InvalidVideoException.Reason.INVALID_TITLE
            !VideoPolicy.isValidDescription(trimmedDescription) -> InvalidVideoException.Reason.INVALID_DESCRIPTION
            else -> null
        }
        if (invalidReason != null) return Result.failure(InvalidVideoException(invalidReason))

        return repository.addVideoLink(
            NewStreamerVideoLink(
                url = VideoPolicy.normalizeLink(url),
                title = title.trim(),
                description = trimmedDescription,
            ),
        )
    }
}
