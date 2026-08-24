package com.strimup.feature.filter.presentation.create

import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.feature.filter.domain.entity.FilterCriteria
import com.strimup.feature.filter.domain.entity.FilterOptionsEntity

sealed interface CreateFilterUiState {
    data object Loading : CreateFilterUiState

    data class Content(
        val filterName: String = "",
        val nameError: String? = null,
        val criteria: FilterCriteria = FilterCriteria(),
        val isSubmitting: Boolean = false,
        val availableOptions: FilterOptionsEntity? = null,

        val availableCategories: List<TagEntity> = emptyList(),
        val selectedCategory: TagEntity? = null,
        val availableTags: List<TagEntity> = emptyList(),

        val activeEdit: ActiveEditType? = null,
    ) : CreateFilterUiState {
        val isFormValid: Boolean
            get() = filterName.isNotBlank() && nameError == null && !isSubmitting
    }
}

sealed interface ActiveEditType {
    data object FilterName : ActiveEditType
    data object AgeRange : ActiveEditType
    data object Personalities : ActiveEditType
    data object StreamFrequency : ActiveEditType
    data object AverageViewers : ActiveEditType
    data object Languages : ActiveEditType
    data object Platforms : ActiveEditType
}