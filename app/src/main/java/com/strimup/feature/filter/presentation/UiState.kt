package com.strimup.feature.filter.presentation

import com.strimup.feature.filter.domain.entity.FilterEntity

data class UiState(
    val isLoading: Boolean = false,
    val filters: List<FilterEntity> = emptyList(),
    val errorMessage: String? = null
)
