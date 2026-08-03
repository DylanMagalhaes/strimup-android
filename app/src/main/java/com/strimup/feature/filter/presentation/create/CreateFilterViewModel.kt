package com.strimup.feature.filter.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.filter.domain.usecase.CreateFilterUsecase
import com.strimup.feature.filter.domain.usecase.GetFilterOptionsUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CreateFilterViewModel @Inject constructor(
    private val createFilter: CreateFilterUsecase,
    private val getFilterOptionsUsecase: GetFilterOptionsUsecase
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadFilterOptions()
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            getFilterOptionsUsecase()
                .onSuccess { options ->
                    _state.update {
                        it.copy(filterOptions = options)
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