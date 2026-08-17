package com.strimup.feature.filter.presentation.matchedstreamer

data class UiState(
    val loading: Boolean = false,
    val streamers: List<com.strimup.common.domain.entity.StreamerEntity> = emptyList(),
    val errorMessage: String? = null
)
