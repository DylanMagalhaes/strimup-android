package com.strimup.feature.streamerprofile.presentation

import com.strimup.feature.streamerprofile.domain.entity.StreamerOptionsEntity
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity

sealed interface EditProfileUiState {
    data object Loading : EditProfileUiState

    data class Success(
        val originalProfile: StreamerProfileEntity,

        val availableOptions: StreamerOptionsEntity,

        val imageUrl: Any,
        val bio: String,
        val dailyStatus: String,
        val selectedLanguages: List<String>,
        val selectedTags: List<StreamerProfileEntity.Tag>,
        val socials: List<StreamerProfileEntity.Social>,
        val personality: String?,
        val personalitySecondary: String?,
        val streamFrequency: String?,
        val averageViewers: String?,
        val activeEdit: ActiveEditType? = null,
        val isSaving: Boolean = false,
        val error: String? = null,
        val isSaveSuccess: Boolean = false
    ) : EditProfileUiState

    data class Error(val message: String) : EditProfileUiState
}

sealed interface ActiveEditType {
    data object Bio : ActiveEditType
    data object DailyStatus : ActiveEditType
    data object PrimaryPersonality : ActiveEditType
    data object SecondaryPersonality : ActiveEditType
    data object StreamFrequency : ActiveEditType
    data object AverageViewers : ActiveEditType
    data object Languages : ActiveEditType
    data class Social(val type: StreamerProfileEntity.Social.Type) : ActiveEditType
}