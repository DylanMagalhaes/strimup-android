package com.strimup.feature.streamervideos.presentation.videossection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.R
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toMessageRes
import com.strimup.core.ui.text.UiText
import com.strimup.feature.streamervideos.domain.entity.VideoPolicy
import com.strimup.feature.streamervideos.domain.entity.VideoUploadEvent
import com.strimup.feature.streamervideos.domain.usecase.DeleteVideoUseCase
import com.strimup.feature.streamervideos.domain.usecase.GetMyVideosUseCase
import com.strimup.feature.streamervideos.domain.usecase.SelectVideoFileUseCase
import com.strimup.feature.streamervideos.domain.usecase.UploadVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideosSectionViewModel @Inject constructor(
    private val getMyVideos: GetMyVideosUseCase,
    private val selectVideoFile: SelectVideoFileUseCase,
    private val uploadVideo: UploadVideoUseCase,
    private val deleteVideo: DeleteVideoUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<VideosSectionUiState>(VideosSectionUiState.Loading)
    val state: StateFlow<VideosSectionUiState> = _state.asStateFlow()

    private val _addVideoState = MutableStateFlow(AddVideoUiState())
    val addVideoState: StateFlow<AddVideoUiState> = _addVideoState.asStateFlow()

    private val _events = Channel<VideosSectionUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var uploadJob: Job? = null

    fun loadVideos() {
        viewModelScope.launch {
            _state.value = VideosSectionUiState.Loading

            getMyVideos()
                .onSuccess { videos -> _state.value = VideosSectionUiState.Success(videos = videos) }
                .onFailure { exception ->
                    _state.value = VideosSectionUiState.Error(messageRes = exception.toDomainError().toMessageRes())
                }
        }
    }

    fun onVideoPicked(uri: String?) {
        val currentState = _state.value
        if (uri == null || currentState !is VideosSectionUiState.Success || !currentState.canAddVideo) return

        viewModelScope.launch {
            selectVideoFile(uri)
                .onSuccess { file -> _addVideoState.value = AddVideoUiState(file = file) }
                .onFailure { exception ->
                    _events.send(VideosSectionUiEvent.ShowMessage(exception.toVideoErrorUiText()))
                }
        }
    }

    fun onTitleChange(title: String) {
        _addVideoState.update { it.copy(title = title.take(VideoPolicy.MAX_TITLE_LENGTH), errorMessage = null) }
    }

    fun onDescriptionChange(description: String) {
        _addVideoState.update {
            it.copy(description = description.take(VideoPolicy.MAX_DESCRIPTION_LENGTH), errorMessage = null)
        }
    }

    fun onUploadClick() {
        val currentState = _addVideoState.value
        val file = currentState.file
        if (!currentState.isSubmitEnabled || file == null || uploadJob?.isActive == true) return

        _addVideoState.update { it.copy(isUploading = true, progressPercent = 0, errorMessage = null) }

        uploadJob = viewModelScope.launch {
            uploadVideo(file, currentState.title, currentState.description)
                .catch { exception ->
                    _addVideoState.update {
                        it.copy(
                            isUploading = false,
                            progressPercent = null,
                            errorMessage = exception.toVideoErrorUiText(),
                        )
                    }
                }
                .collect { event ->
                    when (event) {
                        is VideoUploadEvent.Progress -> {
                            _addVideoState.update { it.copy(progressPercent = event.percent) }
                        }

                        is VideoUploadEvent.Completed -> {
                            _addVideoState.value = AddVideoUiState()
                            updateSuccess { success ->
                                success.copy(videos = (success.videos + event.video).sortedBy { it.order })
                            }
                            _events.send(VideosSectionUiEvent.ShowMessage(UiText.Resource(R.string.videos_added)))
                        }
                    }
                }
        }
    }

    fun onCancelUploadClick() {
        if (uploadJob?.isActive != true) return

        uploadJob?.cancel()
        uploadJob = null
        _addVideoState.update {
            it.copy(
                isUploading = false,
                progressPercent = null,
                errorMessage = UiText.Resource(R.string.videos_upload_cancelled),
            )
        }
    }

    fun onAddDismiss() {
        if (!_addVideoState.value.isUploading) {
            _addVideoState.value = AddVideoUiState()
        }
    }

    fun onDeleteConfirm(videoId: String) {
        val currentState = _state.value
        if (currentState !is VideosSectionUiState.Success || videoId in currentState.deletingVideoIds) return

        updateSuccess { it.copy(deletingVideoIds = it.deletingVideoIds + videoId) }

        viewModelScope.launch {
            deleteVideo(videoId)
                .onSuccess {
                    updateSuccess { success ->
                        success.copy(
                            videos = success.videos.filterNot { it.id == videoId },
                            deletingVideoIds = success.deletingVideoIds - videoId,
                        )
                    }
                }
                .onFailure { exception ->
                    updateSuccess { it.copy(deletingVideoIds = it.deletingVideoIds - videoId) }
                    _events.send(VideosSectionUiEvent.ShowMessage(exception.toVideoErrorUiText()))
                }
        }
    }

    private fun updateSuccess(transform: (VideosSectionUiState.Success) -> VideosSectionUiState.Success) {
        _state.update { state ->
            if (state is VideosSectionUiState.Success) transform(state) else state
        }
    }
}
