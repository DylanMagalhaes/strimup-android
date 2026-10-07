package com.strimup.feature.streamervideos.presentation.videossection

import com.strimup.core.ui.text.UiText
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.VideoPolicy

data class AddVideoUiState(
    val file: LocalVideoFile? = null,
    val title: String = "",
    val description: String = "",
    val isUploading: Boolean = false,
    val progressPercent: Int? = null,
    val errorMessage: UiText? = null,
) {
    val isVisible: Boolean
        get() = file != null

    val isSubmitEnabled: Boolean
        get() = !isUploading &&
            file != null &&
            VideoPolicy.isValidTitle(title) &&
            VideoPolicy.isValidDescription(description)
}
