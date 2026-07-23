package com.strimup.feature.streamerprofile.presentation

import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity

data class ProfileUiState(
    val loading: Boolean = true,
    val streamer: StreamerProfileEntity? = null
)