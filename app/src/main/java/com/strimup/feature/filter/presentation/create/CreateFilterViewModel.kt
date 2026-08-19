package com.strimup.feature.filter.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.core.tag.domain.usecase.GetTagsUsecase
import com.strimup.feature.filter.domain.usecase.CreateFilterUsecase
import com.strimup.feature.filter.domain.usecase.GetFilterOptionsUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CreateFilterViewModel @Inject constructor(
    private val createFilter: CreateFilterUsecase,
    private val getFilterOptionsUsecase: GetFilterOptionsUsecase,
    private val getTags: GetTagsUsecase
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _event = MutableSharedFlow<UiEvent>()
    val events = _event.asSharedFlow()

    private var fetchedTags: List<TagEntity> = emptyList()

    init {
        viewModelScope.launch {
            loadFilterOptions()
            loadTags()
        }
    }

    fun saveFilter() {
        val filterName = _state.value.filterName.trim()
        val criteria = _state.value.criteria

        if (filterName.isBlank()) {
            _state.update { it.copy(nameError = "Le nom du filtre ne peut pas être vide") }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSubmitting = true,
                    errorMessage = null,
                    nameError = null
                )
            }

            createFilter(filterName, criteria)
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                        )
                    }
                    _event.emit(UiEvent.Success)
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.localizedMessage ?: "Erreur lors de la création du filtre"
                        )
                    }
                }
        }
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            getFilterOptionsUsecase()
                .onSuccess { options ->
                    _state.update {
                        it.copy(availableOptions = options)
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            errorMessage = error.localizedMessage ?: "Impossible de charger les options"
                        )
                    }
                }
        }
    }

    private fun loadTags() {
        viewModelScope.launch {
            getTags()
                .onSuccess { tags ->
                    fetchedTags = tags
                    val categories = tags.distinctBy { it.category }
                    val defaultCategory = categories.firstOrNull()

                    _state.update { currentState ->
                        currentState.copy(
                            availableCategories = categories,
                            selectedCategory = currentState.selectedCategory ?: defaultCategory,
                            availableTags = if (currentState.selectedCategory != null) {
                                tags.filter { it.category == currentState.selectedCategory?.category }
                            } else {
                                tags.filter { it.category == defaultCategory?.category }
                            }
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { currentState ->
                        currentState.copy(
                            errorMessage = error.localizedMessage ?: "Impossible de charger les tags"
                        )
                    }
                }
        }
    }

    fun onTagSelected(tag: TagEntity) {
        _state.update { currentState ->
            val currentTags = currentState.criteria.tags
            val isAlreadySelected = currentTags.any { it.id == tag.id }

            val updatedTags = if (isAlreadySelected) {
                currentTags.filterNot { it.id == tag.id }
            } else if (currentTags.size < 5) {
                currentTags + tag
            } else {
                currentTags
            }

            currentState.copy(
                criteria = currentState.criteria.copy(
                    tags = updatedTags
                )
            )
        }
    }

    fun onRangeSelected(range: IntRange) {
        _state.update { currentState ->
            currentState.copy(
                criteria = currentState.criteria.copy(
                    ageRange = range
                )
            )
        }
    }

    fun onCategorySelected(categorySelected: TagEntity) {
        _state.update { currentState ->
            currentState.copy(
                selectedCategory = categorySelected,
                availableTags = fetchedTags.filter {
                    it.category == categorySelected.category
                }
            )
        }
    }

    fun onFilterNameChange(name: String) {
        _state.update { it.copy(filterName = name) }
    }

    fun onPersonalitySelected(newPersonality: String) {
        _state.update { currentState ->
            val currentPersonalities = currentState.criteria.personalities
            val updatedPersonalities = if (currentPersonalities.contains(newPersonality)) {
                currentPersonalities - newPersonality
            } else {
                currentPersonalities + newPersonality
            }

            currentState.copy(
                criteria = currentState.criteria.copy(personalities = updatedPersonalities)
            )
        }
    }

    fun onAverageViewersSelected(newAverageViewers: String) {
        _state.update {
            it.copy(
                criteria = it.criteria.copy(averageViewers = newAverageViewers)
            )
        }
    }

    fun onStreamFrequencySelected(newStreamFrequency: String) {
        _state.update {
            it.copy(
                criteria = it.criteria.copy(streamFrequency = newStreamFrequency)
            )
        }
    }

    fun onLanguagesSelected(newLanguage: String) {
        _state.update { currentState ->
            val currentLanguages = currentState.criteria.languages
            val updateLanguages = if (currentLanguages.contains(newLanguage)) {
                currentLanguages - newLanguage
            } else {
                currentLanguages + newLanguage
            }

            currentState.copy(
                criteria = currentState.criteria.copy(languages = updateLanguages)
            )
        }
    }

    fun onPlatformSelected(platform: String) {
        _state.update { currentState ->
            val currentPlatforms = currentState.criteria.platforms
            val updatePlatforms = if (currentPlatforms.contains(platform)) {
                currentPlatforms - platform
            } else {
                currentPlatforms + platform
            }

            currentState.copy(
                criteria = currentState.criteria.copy(platforms = updatePlatforms)
            )
        }
    }

    fun openEdit(editType: ActiveEditType) {
        _state.update { it.copy(activeEdit = editType) }
    }

    fun dismissEdit() {
        _state.update { it.copy(activeEdit = null) }
    }
}