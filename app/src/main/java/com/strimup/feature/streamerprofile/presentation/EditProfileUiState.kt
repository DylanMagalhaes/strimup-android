package com.strimup.feature.streamerprofile.presentation

import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity

sealed interface EditProfileUiState {
    object Loading : EditProfileUiState

    data class Success(
        val originalProfile: StreamerProfileEntity,

        val bio: String,
        val dailyStatus: String,
        val selectedLanguages: List<String>,
        val selectedTags: List<StreamerProfileEntity.Tag>,
        val socials: List<StreamerProfileEntity.Social>,
        val personality: String?,
        val personalitySecondary: String?,
        val streamFrequency: String?,
        val averageViewers: String?,

        val isSaving: Boolean = false,
        val error: String? = null,
        val isSaveSuccess: Boolean = false
    ) : EditProfileUiState

    data class Error(val message: String) : EditProfileUiState
}
