package com.strimup.feature.streamervideos.presentation.videossection

import com.strimup.core.ui.text.UiText

sealed interface VideosSectionUiEvent {
    data class ShowMessage(val text: UiText) : VideosSectionUiEvent
}
