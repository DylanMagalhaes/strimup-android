package com.strimup.feature.filter.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.tag.domain.entity.TagEntity
import com.strimup.core.tag.domain.usecase.GetTagsUsecase
import com.strimup.feature.filter.domain.usecase.CreateFilterUsecase
import com.strimup.feature.filter.domain.usecase.GetFilterOptionsUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CreateFilterViewModel @Inject constructor(
    private val createFilter: CreateFilterUsecase,
    private val getFilterOptionsUsecase: GetFilterOptionsUsecase,
    private val getTags: GetTagsUsecase
) : ViewModel() {

    private val _state = MutableStateFlow<CreateFilterUiState>(CreateFilterUiState.Loading)
    val state: StateFlow<CreateFilterUiState> = _state.asStateFlow()

    private val _events = Channel<CreateFilterUiEvent>()
    val events = _events.receiveAsFlow()

    private var fetchedTags: List<TagEntity> = emptyList()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.value = CreateFilterUiState.Loading

            val optionsDeferred = async { getFilterOptionsUsecase() }
            val tagsDeferred = async { getTags() }

            val optionsResult = optionsDeferred.await()
            val tagsResult = tagsDeferred.await()

            var contentState = CreateFilterUiState.Content()

            optionsResult
                .onSuccess { options ->
                    contentState = contentState.copy(availableOptions = options)
                }
                .onFailure { error ->
                    _events.send(CreateFilterUiEvent.ShowSnackBar(error.localizedMessage ?: "Impossible de charger les options"))
                }

            tagsResult
                .onSuccess { tags ->
                    fetchedTags = tags
                    val categories = tags.distinctBy { it.category }
                    val defaultCategory = categories.firstOrNull()

                    contentState = contentState.copy(
                        availableCategories = categories,
                        selectedCategory = defaultCategory,
                        availableTags = tags.filter { it.category == defaultCategory?.category }
                    )
                }
                .onFailure { error ->
                    _events.send(CreateFilterUiEvent.ShowSnackBar(error.localizedMessage ?: "Impossible de charger les tags"))
                }

            _state.value = contentState
        }
    }

    fun saveFilter() {
        val currentState = _state.value as? CreateFilterUiState.Content ?: return
        val filterName = currentState.filterName.trim()
        val criteria = currentState.criteria

        if (filterName.isBlank()) {
            updateContentState { it.copy(nameError = "Le nom du filtre ne peut pas être vide") }
            return
        }

        viewModelScope.launch {
            updateContentState {
                it.copy(
                    isSubmitting = true,
                    nameError = null
                )
            }

            createFilter(filterName, criteria)
                .onSuccess {
                    updateContentState { it.copy(isSubmitting = false) }
                    _events.send(CreateFilterUiEvent.FilterCreated)
                }
                .onFailure { error ->
                    updateContentState { it.copy(isSubmitting = false) }
                    val message = error.localizedMessage ?: "Erreur lors de la création du filtre"
                    _events.send(CreateFilterUiEvent.ShowSnackBar(message))
                }
        }
    }

    fun onTagSelected(tag: TagEntity) {
        updateContentState { currentState ->
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
                criteria = currentState.criteria.copy(tags = updatedTags)
            )
        }
    }

    fun onRangeSelected(range: IntRange) {
        updateContentState { currentState ->
            currentState.copy(
                criteria = currentState.criteria.copy(ageRange = range)
            )
        }
    }

    fun onCategorySelected(categorySelected: TagEntity) {
        updateContentState { currentState ->
            currentState.copy(
                selectedCategory = categorySelected,
                availableTags = fetchedTags.filter { it.category == categorySelected.category }
            )
        }
    }

    fun onFilterNameChange(name: String) {
        updateContentState { currentState ->
            currentState.copy(
                filterName = name,
                nameError = if (name.isNotBlank()) null else currentState.nameError
            )
        }
    }

    fun onPersonalitySelected(newPersonality: String) {
        updateContentState { currentState ->
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
        updateContentState { currentState ->
            currentState.copy(
                criteria = currentState.criteria.copy(averageViewers = newAverageViewers)
            )
        }
    }

    fun onStreamFrequencySelected(newStreamFrequency: String) {
        updateContentState { currentState ->
            currentState.copy(
                criteria = currentState.criteria.copy(streamFrequency = newStreamFrequency)
            )
        }
    }

    fun onLanguagesSelected(newLanguage: String) {
        updateContentState { currentState ->
            val currentLanguages = currentState.criteria.languages
            val updatedLanguages = if (currentLanguages.contains(newLanguage)) {
                currentLanguages - newLanguage
            } else {
                currentLanguages + newLanguage
            }

            currentState.copy(
                criteria = currentState.criteria.copy(languages = updatedLanguages)
            )
        }
    }

    fun onPlatformSelected(platform: String) {
        updateContentState { currentState ->
            val currentPlatforms = currentState.criteria.platforms
            val updatedPlatforms = if (currentPlatforms.contains(platform)) {
                currentPlatforms - platform
            } else {
                currentPlatforms + platform
            }

            currentState.copy(
                criteria = currentState.criteria.copy(platforms = updatedPlatforms)
            )
        }
    }

    fun openEdit(editType: ActiveEditType) {
        updateContentState { it.copy(activeEdit = editType) }
    }

    fun dismissEdit() {
        updateContentState { it.copy(activeEdit = null) }
    }

    private fun updateContentState(transform: (CreateFilterUiState.Content) -> CreateFilterUiState.Content) {
        _state.update { currentState ->
            if (currentState is CreateFilterUiState.Content) {
                transform(currentState)
            } else {
                currentState
            }
        }
    }
}