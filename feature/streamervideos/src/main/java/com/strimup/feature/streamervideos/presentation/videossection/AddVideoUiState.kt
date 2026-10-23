package com.strimup.feature.streamervideos.presentation.videossection

import com.strimup.core.ui.text.UiText
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.VideoPolicy

enum class AddVideoMode {
    FILE,
    LINK,
}

data class AddVideoUiState(
    val isOpen: Boolean = false,
    val mode: AddVideoMode = AddVideoMode.FILE,
    val file: LocalVideoFile? = null,
    val url: String = "",
    val title: String = "",
    val description: String = "",
    val isUploading: Boolean = false,
    val progressPercent: Int? = null,
    val errorMessage: UiText? = null,
) {
    val isVisible: Boolean
        get() = isOpen || file != null

    val isLinkInvalid: Boolean
        get() = url.isNotBlank() && !VideoPolicy.isSupportedLink(url)

    val isSubmitEnabled: Boolean
        get() = !isUploading &&
            hasSource &&
            VideoPolicy.isValidTitle(title) &&
            VideoPolicy.isValidDescription(description)

    private val hasSource: Boolean
        get() = when (mode) {
            AddVideoMode.FILE -> file != null
            AddVideoMode.LINK -> VideoPolicy.isSupportedLink(url)
        }
}
