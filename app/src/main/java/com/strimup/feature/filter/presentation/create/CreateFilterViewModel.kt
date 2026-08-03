package com.strimup.feature.filter.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.common.domain.entity.TagEntity
import com.strimup.feature.filter.domain.usecase.CreateFilterUsecase
import com.strimup.feature.filter.domain.usecase.GetFilterOptionsUsecase
import com.strimup.common.domain.usecase.GetTagsUsecase // Ou le package où se trouve ton usecase partagé
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private var fetchedTags: List<TagEntity> = emptyList()

    init {
        viewModelScope.launch {
            loadFilterOptions()
            loadTags()
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
                    _state.update { currentState ->
                        currentState.copy(
                            availableCategories = tags.distinctBy { it.category },
                            availableTags = tags
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

    fun onTagSelected(tag: TagEntity) {
        _state.update { currentState ->
            val currentTags = currentState.selectedFilterTags

            val updatedTags = if (currentTags.contains(tag)) {
                currentTags - tag
            } else if (currentTags.size < 5) {
                currentTags + tag
            } else {
                currentTags
            }

            currentState.copy(
                selectedFilterTags = updatedTags
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

    fun onStatusSelected(newStatus: String) {
        _state.update {
            it.copy(
                criteria = it.criteria.copy(status = newStatus)
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