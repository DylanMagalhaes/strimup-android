package com.strimup.feature.streamerprofile.presentation.editeprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.common.user.domain.usecase.GetUserFlowUseCase
import com.strimup.feature.streamerprofile.domain.entity.StreamerOptionsEntity
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity
import com.strimup.feature.streamerprofile.domain.entity.TagEntity
import com.strimup.feature.streamerprofile.domain.usecase.DefaultGetTags
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
    private val getOptions: GetStreamerOptionsUseCase,
    private val getTags: DefaultGetTags,
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileUiState())
    val state: StateFlow<EditProfileUiState> = _state.asStateFlow()

    private var fetchedOptions: StreamerOptionsEntity? = null
    private var fetchedTags: List<TagEntity> = emptyList()

    init {
        viewModelScope.launch {
            loadOptions()
            loadTags()

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
                        currentState.copy(availableOptions = options)
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
        }
    }

    private fun loadStreamer(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            getStreamer(id)
                .onSuccess { streamer ->
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            originalProfile = streamer,
                            availableTags = fetchedTags,
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
                }
                .onFailure {
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = "Erreur pendant la récupération du profil"
                        )
                    }
                }
        }
    }

    fun onImageSelected(newPhoto: Any) {
        _state.update { currentState ->
            currentState.copy(imageUrl = newPhoto)
        }
    }

    fun onBioChanged(newBio: String) {
        _state.update { currentState ->
            currentState.copy(bio = newBio)
        }
    }

    fun onDailyStatusChanged(newStatus: String) {
        _state.update { currentState ->
            currentState.copy(dailyStatus = newStatus)
        }
    }

    fun onCategorySelected(categorySelected: TagEntity) {
        _state.update { it ->
            it.copy(
                selectedCategory = categorySelected,
                availableTags = fetchedTags.filter {
                    it.category == categorySelected.category
                }
            )
        }
    }

    fun onTagSelected(tag: TagEntity) {
        _state.update { currentState ->
            val currentTags = currentState.selectedTags

            val updatedTags = if (currentTags.contains(tag)) {
                currentTags - tag
            } else if (currentTags.size < 4) {
                currentTags + tag
            } else {
                currentTags
            }

            currentState.copy(
                selectedTags = updatedTags
            )
        }
    }

    fun onLanguageSelected(language: String) {
        _state.update { currentState ->
            val currentLanguages = currentState.selectedLanguages

            val updatedLanguages = if (currentLanguages.contains(language)) {
                currentLanguages - language
            } else {
                currentLanguages + language
            }
            currentState.copy(selectedLanguages = updatedLanguages)
        }
    }


    fun onPrimaryPersonalityChanged(personality: String) {
        _state.update { currentState ->
            val updatedSecondary = if (currentState.personalitySecondary == personality) {
                null
            } else {
                currentState.personalitySecondary
            }

            currentState.copy(
                personality = personality,
                personalitySecondary = updatedSecondary
            )
        }
    }

    fun onSecondaryPersonalityChanged(personality: String) {
        _state.update { currentState ->
            val updatedPrimary = if (currentState.personality == personality) {
                null
            } else {
                currentState.personality
            }

            currentState.copy(
                personality = updatedPrimary,
                personalitySecondary = personality
            )
        }
    }

    fun onAverageViewersChanged(average: String) {
        _state.update { currentState ->
            currentState.copy(averageViewers = average)
        }
    }

    fun onStreamFrequencyChanged(frequency: String) {
        _state.update { currentState ->
            currentState.copy(streamFrequency = frequency)
        }
    }

    fun onSocialUrlChanged(url: String, targetSocial: StreamerProfileEntity.Social.Type) {
        _state.update { currentState ->
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
                    currentState.socials + StreamerProfileEntity.Social(
                        url = cleanUrl,
                        type = targetSocial
                    )
                } else {
                    currentState.socials
                }
            }

            currentState.copy(socials = updatedSocials)
        }
    }

    fun openEdit(editType: ActiveEditType) {
        _state.update { it.copy(activeEdit = editType) }
    }

    fun dismissEdit() {
        _state.update { it.copy(activeEdit = null) }
    }

    fun saveProfile() {
        val currentState = _state.value
        val originalProfile = currentState.originalProfile ?: return

        viewModelScope.launch {
            _state.update { state ->
                state.copy(isSaving = true, isSaveSuccess = false, errorMessage = null)
            }

            var finalImageUrl = originalProfile.imageUrl
            val selectedImage = currentState.imageUrl.toString()

            if (selectedImage.startsWith("content://") || selectedImage.startsWith("file://")) {
                val avatarResult = updateAvatar(selectedImage)

                if (avatarResult.isFailure) {
                    _state.update { state ->
                        state.copy(
                            isSaving = false,
                            errorMessage = avatarResult.exceptionOrNull()?.localizedMessage
                                ?: "Erreur lors de l'envoi de la photo de profil"
                        )
                    }
                    return@launch
                }

                avatarResult.getOrNull()?.let { uploadedUrl ->
                    finalImageUrl = uploadedUrl
                }
            }

            val updatedProfile = originalProfile.copy(
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
                        state.copy(
                            isSaving = false,
                            isSaveSuccess = true,
                            originalProfile = updatedStreamer,
                            imageUrl = updatedStreamer.imageUrl
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update { state ->
                        state.copy(
                            isSaving = false,
                            errorMessage = exception.localizedMessage ?: "Impossible de sauvegarder les modifications"
                        )
                    }
                }
        }
    }
}