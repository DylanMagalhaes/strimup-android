package com.strimup.feature.streamerprofile.presentation.streamerprofile

import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity

data class ProfileUiState(
    val loading: Boolean = true,
    val streamer: StreamerProfileEntity? = null
)