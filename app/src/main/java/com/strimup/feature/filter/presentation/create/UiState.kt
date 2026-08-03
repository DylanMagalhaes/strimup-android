package com.strimup.feature.filter.presentation.create

import com.strimup.feature.filter.domain.entity.FilterCriteria

data class UiState(
    val name: String = "",
    val nameError: String? = null,
    val criteria: FilterCriteria = FilterCriteria(),
    val isSubmitting: Boolean = false,
    val isFormValid: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)