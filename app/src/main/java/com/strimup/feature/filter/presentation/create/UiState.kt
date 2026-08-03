package com.strimup.feature.filter.presentation.create

import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterOptionsEntity

data class UiState(
    val filterName: String = "",
    val nameError: String? = null,
    val criteria: FilterCriteria = FilterCriteria(),
    val isSubmitting: Boolean = false,
    val isFormValid: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val filterOptions: FilterOptionsEntity? = null,

    val activeEdit: ActiveEditType? = null,
)

sealed interface ActiveEditType {
    data object FilterName : ActiveEditType
    data object AgeRange : ActiveEditType
    data object Personalities : ActiveEditType
    data object StreamFrequency : ActiveEditType
    data object AverageViewers : ActiveEditType
    data object Languages : ActiveEditType
    data object Platforms : ActiveEditType
}