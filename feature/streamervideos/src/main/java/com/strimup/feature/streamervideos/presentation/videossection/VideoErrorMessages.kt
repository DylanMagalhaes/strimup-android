package com.strimup.feature.streamervideos.presentation.videossection

import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toUiText
import com.strimup.core.ui.text.UiText
import com.strimup.feature.streamervideos.R
import com.strimup.feature.streamervideos.domain.entity.InvalidVideoException

internal fun Throwable.toVideoErrorUiText(): UiText = when (this) {
    is InvalidVideoException -> UiText.Resource(
        when (reason) {
            InvalidVideoException.Reason.UNSUPPORTED_FORMAT -> R.string.videos_error_format
            InvalidVideoException.Reason.FILE_TOO_LARGE -> R.string.videos_error_size
            InvalidVideoException.Reason.INVALID_TITLE -> R.string.videos_error_title
            InvalidVideoException.Reason.INVALID_DESCRIPTION -> R.string.videos_error_description
        },
    )
    else -> toDomainError().toUiText()
}
