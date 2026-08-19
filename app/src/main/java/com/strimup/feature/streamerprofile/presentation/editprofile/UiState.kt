package com.strimup.feature.streamerprofile.presentation.editprofile

import com.strimup.core.streamer.domain.entity.Social
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.streamer.domain.entity.StreamerOptions
import com.strimup.core.tag.domain.entity.TagEntity

data class EditProfileUiState(

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val errorMessage: String? = null,

    val originalProfile: Streamer? = null,
    val availableOptions: StreamerOptions? = null,
    val availableCategories: List<TagEntity> = emptyList(),
    val availableTags: List<TagEntity> = emptyList(),

    val imageUrl: Any? = null,
    val bio: String = "",
    val dailyStatus: String = "",
    val selectedLanguages: List<String> = emptyList(),
    val selectedTags: List<TagEntity> = emptyList(),
    val selectedCategory: TagEntity? = null,
    val socials: List<Social> = emptyList(),
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
    data class SocialEdit(val type: Social.Type) : ActiveEditType
}
