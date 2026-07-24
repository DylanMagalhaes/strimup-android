package com.strimup.feature.streamerprofile.presentation

import com.strimup.feature.streamerprofile.domain.entity.StreamerOptionsEntity
import com.strimup.feature.streamerprofile.domain.entity.StreamerProfileEntity
import com.strimup.feature.streamerprofile.domain.entity.TagEntity

data class EditProfileUiState(

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val errorMessage: String? = null,

    val originalProfile: StreamerProfileEntity? = null,
    val availableOptions: StreamerOptionsEntity? = null,
    val availableTags: List<TagEntity> = emptyList(),

    val imageUrl: Any? = null,
    val bio: String = "",
    val dailyStatus: String = "",
    val selectedLanguages: List<String> = emptyList(),
    val selectedTags: List<TagEntity> = emptyList(),
    val socials: List<StreamerProfileEntity.Social> = emptyList(),
    val personality: String? = null,
    val personalitySecondary: String? = null,
    val streamFrequency: String? = null,
    val averageViewers: String? = null,

    val activeEdit: ActiveEditType? = null,
)

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
