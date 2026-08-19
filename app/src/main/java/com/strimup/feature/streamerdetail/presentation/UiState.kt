package com.strimup.feature.streamerdetail.presentation

import com.strimup.core.streamer.domain.entity.Streamer

data class UiState(
    val loading: Boolean = true,
    val streamer: Streamer? = null
)
