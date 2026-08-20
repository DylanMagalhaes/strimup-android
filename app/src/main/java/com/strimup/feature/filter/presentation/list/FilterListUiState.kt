package com.strimup.feature.filter.presentation.list

import com.strimup.feature.filter.domain.entity.FilterEntity

data class FilterListUiState(
    val filters: List<FilterEntity> = emptyList(),
    val isLoading: Boolean = true,
) {
    val isEmpty: Boolean get() = !isLoading && filters.isEmpty()
}