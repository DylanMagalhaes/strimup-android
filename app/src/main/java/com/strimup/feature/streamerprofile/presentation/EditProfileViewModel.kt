package com.strimup.feature.streamerprofile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.common.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.streamerprofile.domain.entity.StreamerOptionsEntity
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity
import com.strimup.feature.streamerprofile.domain.usecase.DefaultUpdateAvatarUsecase
import com.strimup.feature.streamerprofile.domain.usecase.DefaultUpdateProfileUsecase
import com.strimup.feature.streamerprofile.domain.usecase.GetStreamerOptionsUseCase
import com.strimup.feature.streamerprofile.domain.usecase.GetStreamerUsecase
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
    private val updateProfile: DefaultUpdateProfileUsecase,
    private val updateAvatar: DefaultUpdateAvatarUsecase,
    private val getUser: GetUserFlowUseCase,
    private val getOptions: GetStreamerOptionsUseCase
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
                        averageViewers = streamer.averageViewers,
                        imageUrl = streamer.imageUrl
                    )
                }
                .onFailure {
                    _state.value = EditProfileUiState.Error(
                        message = "Erreur pendant la récupération du profil"
                    )
                }
        }
    }

    fun onImageSelected(newPhoto: Any) {
        _state.update { currentState ->
            if (currentState is EditProfileUiState.Success) {
                currentState.copy(imageUrl = newPhoto)
            } else currentState
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

    fun onSocialUrlChanged(url: String, targetSocial: StreamerProfileEntity.Social.Type) {
        _state.update { currentState ->
            if (currentState is EditProfileUiState.Success) {
                val cleanUrl = url.trim()
                val exists = currentState.socials.any { it.type == targetSocial }

                val updatedSocials = if (exists) {
                    if (cleanUrl.isBlank()) {
                        currentState.socials.filterNot { it.type == targetSocial }
                    } else {
                        currentState.socials.map { social ->
                            if (social.type == targetSocial) {
                                social.copy(url = cleanUrl)
                            } else {
                                social
                            }
                        }
                    }
                } else {
                    if (cleanUrl.isNotBlank()) {
                        currentState.socials + StreamerProfileEntity.Social(url = cleanUrl, type = targetSocial)
                    } else {
                        currentState.socials
                    }
                }

                currentState.copy(socials = updatedSocials)
            } else currentState
        }
    }

    fun saveProfile() {
        val currentState = _state.value
        if (currentState !is EditProfileUiState.Success) return

        viewModelScope.launch {

            _state.update { state ->
                if (state is EditProfileUiState.Success) {
                    state.copy(isSaving = true, isSaveSuccess = false, error = null)
                } else state
            }

            var finalImageUrl = currentState.originalProfile.imageUrl
            val selectedImage = currentState.imageUrl.toString()

            if (selectedImage.startsWith("content://") || selectedImage.startsWith("file://")) {
                val avatarResult = updateAvatar(selectedImage)

                if (avatarResult.isFailure) {
                    _state.update { state ->
                        if (state is EditProfileUiState.Success) {
                            state.copy(
                                isSaving = false,
                                error = avatarResult.exceptionOrNull()?.localizedMessage
                                    ?: "Erreur lors de l'envoi de la photo de profil"
                            )
                        } else state
                    }
                    return@launch
                }

                avatarResult.getOrNull()?.let { uploadedUrl ->
                    finalImageUrl = uploadedUrl
                }
            }

            val updatedProfile = currentState.originalProfile.copy(
                imageUrl = finalImageUrl,
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

            updateProfile(updatedProfile)
                .onSuccess { updatedStreamer ->
                    _state.update { state ->
                        if (state is EditProfileUiState.Success) {
                            state.copy(
                                isSaving = false,
                                isSaveSuccess = true,
                                originalProfile = updatedStreamer,
                                imageUrl = updatedStreamer.imageUrl
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
        }
    }
}