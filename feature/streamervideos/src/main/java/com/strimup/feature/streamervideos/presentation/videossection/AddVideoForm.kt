package com.strimup.feature.streamervideos.presentation.videossection

import com.strimup.feature.streamervideos.domain.entity.VideoPolicy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddVideoForm {
    private val _state = MutableStateFlow(AddVideoUiState())
    val state: StateFlow<AddVideoUiState> = _state.asStateFlow()

    val value: AddVideoUiState
        get() = _state.value

    fun open() {
        _state.value = AddVideoUiState(isOpen = true)
    }

    fun reset() {
        _state.value = AddVideoUiState()
    }

    fun update(transform: (AddVideoUiState) -> AddVideoUiState) {
        _state.update(transform)
    }

    fun onModeChange(mode: AddVideoMode) {
        if (!value.isUploading) update { it.copy(mode = mode, errorMessage = null) }
    }

    fun onUrlChange(url: String) {
        update { it.copy(url = url.take(VideoPolicy.MAX_LINK_LENGTH), errorMessage = null) }
    }

    fun onTitleChange(title: String) {
        update { it.copy(title = title.take(VideoPolicy.MAX_TITLE_LENGTH), errorMessage = null) }
    }

    fun onDescriptionChange(description: String) {
        update { it.copy(description = description.take(VideoPolicy.MAX_DESCRIPTION_LENGTH), errorMessage = null) }
    }

    fun onDismiss() {
        if (!value.isUploading) reset()
    }
}
