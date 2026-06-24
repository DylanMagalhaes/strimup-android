package com.strimup.feature.streamerdetail.presentation

import com.strimup.feature.streamerdetail.domain.entity.StreamerDetailEntity

data class UiState(
    val loading: Boolean = true,
    val streamer: StreamerDetailEntity? = null
)
