package com.strimup.feature.profile.presentation

import com.strimup.feature.profile.domain.entity.ProfileStreamerEntity

data class UiState(
    val loading: Boolean = true,
    val streamer: ProfileStreamerEntity
)
