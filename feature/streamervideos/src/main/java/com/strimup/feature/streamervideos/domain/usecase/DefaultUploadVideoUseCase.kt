package com.strimup.feature.streamervideos.domain.usecase

import com.strimup.feature.streamervideos.domain.StreamerVideoRepository
import com.strimup.feature.streamervideos.domain.entity.InvalidVideoException
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.NewStreamerVideo
import com.strimup.feature.streamervideos.domain.entity.VideoPolicy
import com.strimup.feature.streamervideos.domain.entity.VideoUploadEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DefaultUploadVideoUseCase @Inject constructor(
    private val repository: StreamerVideoRepository,
) : UploadVideoUseCase {
    override fun invoke(file: LocalVideoFile, title: String, description: String): Flow<VideoUploadEvent> = flow {
        emitAll(repository.uploadVideo(validated(file, title, description)))
    }

    private fun validated(file: LocalVideoFile, title: String, description: String): NewStreamerVideo {
        val format = VideoPolicy.formatOf(file)
            ?: throw InvalidVideoException(InvalidVideoException.Reason.UNSUPPORTED_FORMAT)
        val trimmedDescription = description.trim().takeIf { it.isNotEmpty() }
        val invalidReason = when {
            VideoPolicy.isTooLarge(file) -> InvalidVideoException.Reason.FILE_TOO_LARGE
            !VideoPolicy.isValidTitle(title) -> InvalidVideoException.Reason.INVALID_TITLE
            !VideoPolicy.isValidDescription(trimmedDescription) -> InvalidVideoException.Reason.INVALID_DESCRIPTION
            else -> null
        }
        if (invalidReason != null) throw InvalidVideoException(invalidReason)

        return NewStreamerVideo(
            file = file,
            format = format,
            title = title.trim(),
            description = trimmedDescription,
        )
    }
}
