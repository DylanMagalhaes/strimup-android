package com.strimup.feature.streamerprofile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.common.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.streamerprofile.domain.usecase.GetStreamerUsecase
import com.strimup.feature.streamerprofile.domain.usecase.UpdateProfileUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getStreamer: GetStreamerUsecase,
    private val updateProfile: UpdateProfileUsecase,
    private val getUser: GetUserFlowUseCase,

    ) : ViewModel() {

    private val _state = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Loading)
    val state: StateFlow<EditProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getUser().collect { user ->
                val id = user?.id
                if (!id.isNullOrBlank()) {
                    loadStreamer(id)
                }
            }
        }
    }

    private fun loadStreamer(id: String) {
        viewModelScope.launch {
            getStreamer(id)
                .onSuccess { streamer ->
                    _state.value = EditProfileUiState.Success(
                        originalProfile = streamer,
                        bio = streamer.bio ?: "",
                        dailyStatus = streamer.dailyStatus ?: "",
                        selectedLanguages = streamer.languages ?: emptyList(),
                        selectedTags = streamer.tags ?: emptyList(),
                        socials = streamer.socials,
                        personality = streamer.personality,
                        personalitySecondary = streamer.personalitySecondary,
                        streamFrequency = streamer.streamFrequency,
                        averageViewers = streamer.averageViewers
                    )
                }
                .onFailure {
                    _state.value = EditProfileUiState.Error(
                        message = "Erreur pendant la récupération du profile"
                    )
                }
        }
    }

    fun onBioChanged(newBio: String) {
        _state.update { currentState ->
            if (currentState is EditProfileUiState.Success) {
                currentState.copy(bio = newBio)
            } else currentState
        }
    }

    fun onDailyStatusChanged(newStatus: String) {
        _state.update { currentState ->
            if (currentState is EditProfileUiState.Success) {
                currentState.copy(dailyStatus = newStatus)
            } else currentState
        }
    }

    fun onLanguageSelected(language: String) {
        _state.update { currentState ->
            if (currentState is EditProfileUiState.Success) {
                val currentLanguages = currentState.selectedLanguages

                val updatedLanguages = if (currentLanguages.contains(language)) {
                    currentLanguages - language
                } else {
                    currentLanguages + language
                }
                currentState.copy(selectedLanguages = updatedLanguages)
            } else currentState
        }
    }

    fun onPrimaryPersonalityChanged(personality: String) {
        _state.update { currentState ->
            if (currentState is EditProfileUiState.Success) {
                currentState.copy(personality = personality)
            } else currentState
        }
    }

    fun onSecondaryPersonalityChanged(personality: String) {
        _state.update { currentState ->
            if (currentState is EditProfileUiState.Success) {
                currentState.copy(personalitySecondary = personality)
            } else currentState
        }
    }

    fun onAverageViewersChanged(average: String) {
        _state.update { currentState ->
            if (currentState is EditProfileUiState.Success) {
                currentState.copy(averageViewers = average)
            } else currentState
        }
    }

    fun onStreamFrequencyChaged(frequency: String) {
        _state.update { currentState ->
            if (currentState is EditProfileUiState.Success) {
                currentState.copy(streamFrequency = frequency)
            } else currentState
        }
    }

    fun saveProfile() {
        val currentState = _state.value
        if (currentState !is EditProfileUiState.Success) return

        viewModelScope.launch {
            _state.value = currentState.copy(isSaving = true, error = null)

            val updatedProfile = currentState.originalProfile.copy(
                bio = currentState.bio.takeIf { it.isNotBlank() },
                dailyStatus = currentState.dailyStatus.takeIf { it.isNotBlank() },
                languages = currentState.selectedLanguages,
                tags = currentState.selectedTags,
                personality = currentState.personality,
                personalitySecondary = currentState.personalitySecondary,
                averageViewers = currentState.averageViewers,
                streamFrequency = currentState.streamFrequency
            )

            updateProfile(updatedProfile)
                .onSuccess { updatedStreamer ->
                    _state.value = currentState.copy(
                        isSaving = false,
                        isSaveSuccess = true,
                        originalProfile = updatedStreamer
                    )
                }
                .onFailure { exception ->
                    _state.value = currentState.copy(
                        isSaving = false,
                        error = exception.localizedMessage ?: "Impossible de sauvegarder les modifications"
                    )
                }
        }
    }

}