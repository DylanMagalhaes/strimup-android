package com.strimup.feature.streamervideos.presentation.videossection

import androidx.annotation.StringRes
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.feature.streamervideos.domain.entity.VideoPolicy

sealed interface VideosSectionUiState {
    data object Loading : VideosSectionUiState

    data class Success(
        val videos: List<Streamer.Video>,
        val deletingVideoIds: Set<String> = emptySet(),
    ) : VideosSectionUiState {
        val canAddVideo: Boolean
            get() = videos.size < VideoPolicy.MAX_VIDEOS
    }

    data class Error(
        @param:StringRes val messageRes: Int,
    ) : VideosSectionUiState
}
