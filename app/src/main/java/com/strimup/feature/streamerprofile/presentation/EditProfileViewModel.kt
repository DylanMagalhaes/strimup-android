package com.strimup.feature.streamerprofile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.common.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.streamerprofile.domain.entity.StreamerOptionsEntity
import com.strimup.feature.streamerprofile.domain.usecase.GetStreamerOptionsUseCase
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
    private val getOptions: GetStreamerOptionsUseCase,

    ) : ViewModel() {

    private val _state = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Loading)
    val state: StateFlow<EditProfileUiState> = _state.asStateFlow()

    private var fetchedOptions: StreamerOptionsEntity? = null

    init {
        viewModelScope.launch {
            loadOptions()

            getUser().collect { user ->
                val id = user?.id
                if (!id.isNullOrBlank()) {
                    loadStreamer(id)
                }
            }
        }
    }

    private fun loadOptions() {
        viewModelScope.launch {
            getOptions()
                .onSuccess { options ->
                    fetchedOptions = options
                    _state.update { currentState ->
                        if (currentState is EditProfileUiState.Success) {
                            currentState.copy(availableOptions = options)
                        } else currentState
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
                        availableOptions = fetchedOptions ?: StreamerOptionsEntity(
                            averageViewers = emptyList(),
                            languages = emptyList(),
                            personalities = emptyList(),
                            streamFrequencies = emptyList()
                        ),
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
                val updatedSecondary = if (currentState.personalitySecondary == personality) {
                    null
                } else {
                    currentState.personalitySecondary
                }

                currentState.copy(
                    personality = personality,
                    personalitySecondary = updatedSecondary
                )
            } else currentState
        }
    }

    fun onSecondaryPersonalityChanged(personality: String) {
        _state.update { currentState ->
            if (currentState is EditProfileUiState.Success) {
                val updatedPrimary = if (currentState.personality == personality) {
                    null
                } else {
                    currentState.personality
                }

                currentState.copy(
                    personality = updatedPrimary,
                    personalitySecondary = personality
                )
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

    fun onStreamFrequencyChanged(frequency: String) {
        _state.update { currentState ->
            if (currentState is EditProfileUiState.Success) {
                currentState.copy(streamFrequency = frequency)
            } else currentState
        }
    }

    fun saveProfile() {
        val currentState = _state.value
        if (currentState !is EditProfileUiState.Success) {
            return
        }

        viewModelScope.launch {
            _state.update { state ->
                if (state is EditProfileUiState.Success) {
                    state.copy(isSaving = true, isSaveSuccess = false, error = null)
                } else state
            }

            val updatedProfile = currentState.originalProfile.copy(
                bio = currentState.bio.takeIf { it.isNotBlank() },
                dailyStatus = currentState.dailyStatus.takeIf { it.isNotBlank() },
                languages = currentState.selectedLanguages,
                tags = currentState.selectedTags,
                socials = currentState.socials,
                personality = currentState.personality,
                personalitySecondary = currentState.personalitySecondary,
                averageViewers = currentState.averageViewers,
                streamFrequency = currentState.streamFrequency
            )

            try {
                val result = updateProfile(updatedProfile)
                result
                    .onSuccess { updatedStreamer ->
                        _state.update { state ->
                            if (state is EditProfileUiState.Success) {
                                state.copy(
                                    isSaving = false,
                                    isSaveSuccess = true,
                                    originalProfile = updatedStreamer
                                )
                            } else state
                        }
                    }
                    .onFailure { exception ->
                        _state.update { state ->
                            if (state is EditProfileUiState.Success) {
                                state.copy(
                                    isSaving = false,
                                    error = exception.localizedMessage ?: "Impossible de sauvegarder les modifications"
                                )
                            } else state
                        }
                    }
            } catch (t: Throwable) {
                _state.update { state ->
                    if (state is EditProfileUiState.Success) {
                        state.copy(
                            isSaving = false,
                            error = t.localizedMessage ?: "Une erreur inattendue est survenue"
                        )
                    } else state
                }
            }
        }
    }

}