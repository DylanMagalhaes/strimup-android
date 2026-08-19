package com.strimup.feature.streamerprofile.presentation.streamerprofile

import com.strimup.core.streamer.domain.entity.Streamer

data class UiState(
    val loading: Boolean = true,
    val streamer: Streamer? = null
)
