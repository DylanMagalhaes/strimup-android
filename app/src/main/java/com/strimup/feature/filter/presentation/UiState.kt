package com.strimup.feature.filter.presentation

import com.strimup.feature.filter.domain.entity.FilterEntity

data class UiState(
    val filters: List<FilterEntity> = emptyList()
)
