package com.strimup.feature.streamervideos.presentation.videossection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.core.network.toDomainError
import com.strimup.core.streamer.domain.entity.Streamer
import com.strimup.core.ui.error.toMessageRes
import com.strimup.core.ui.text.UiText
import com.strimup.feature.streamervideos.R
import com.strimup.feature.streamervideos.domain.entity.LocalVideoFile
import com.strimup.feature.streamervideos.domain.entity.VideoUploadEvent
import com.strimup.feature.streamervideos.domain.usecase.AddVideoLinkUseCase
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
    private val addVideoLink: AddVideoLinkUseCase,
    private val deleteVideo: DeleteVideoUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<VideosSectionUiState>(VideosSectionUiState.Loading)
    val state: StateFlow<VideosSectionUiState> = _state.asStateFlow()

    val addVideoForm = AddVideoForm()
    val addVideoState: StateFlow<AddVideoUiState> = addVideoForm.state

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

    fun onAddClick() {
        val currentState = _state.value
        if (currentState !is VideosSectionUiState.Success || !currentState.canAddVideo) return

        addVideoForm.open()
    }

    fun onVideoPicked(uri: String?) {
        val currentState = _state.value
        if (uri == null || currentState !is VideosSectionUiState.Success || !currentState.canAddVideo) return

        viewModelScope.launch {
            selectVideoFile(uri)
                .onSuccess { file ->
                    addVideoForm.update {
                        it.copy(isOpen = true, mode = AddVideoMode.FILE, file = file, errorMessage = null)
                    }
                }
                .onFailure { exception ->
                    val message = exception.toVideoErrorUiText()
                    if (addVideoForm.value.isVisible) {
                        addVideoForm.update { it.copy(errorMessage = message) }
                    } else {
                        _events.send(VideosSectionUiEvent.ShowMessage(message))
                    }
                }
        }
    }

    fun onSubmitClick() {
        val currentState = addVideoForm.value
        if (!currentState.isSubmitEnabled || uploadJob?.isActive == true) return

        when (currentState.mode) {
            AddVideoMode.FILE -> currentState.file?.let { file -> upload(file, currentState) }
            AddVideoMode.LINK -> addLink(currentState)
        }
    }

    private fun upload(file: LocalVideoFile, currentState: AddVideoUiState) {
        addVideoForm.update { it.copy(isUploading = true, progressPercent = 0, errorMessage = null) }

        uploadJob = viewModelScope.launch {
            uploadVideo(file, currentState.title, currentState.description)
                .catch { exception ->
                    addVideoForm.update {
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
                            addVideoForm.update { it.copy(progressPercent = event.percent) }
                        }

                        is VideoUploadEvent.Completed -> onVideoAdded(event.video)
                    }
                }
        }
    }

    private fun addLink(currentState: AddVideoUiState) {
        addVideoForm.update { it.copy(isUploading = true, progressPercent = null, errorMessage = null) }

        uploadJob = viewModelScope.launch {
            addVideoLink(currentState.url, currentState.title, currentState.description)
                .onSuccess { video -> onVideoAdded(video) }
                .onFailure { exception ->
                    addVideoForm.update {
                        it.copy(isUploading = false, errorMessage = exception.toVideoErrorUiText())
                    }
                }
        }
    }

    private suspend fun onVideoAdded(video: Streamer.Video) {
        addVideoForm.reset()
        updateSuccess { success -> success.copy(videos = (success.videos + video).sortedBy { it.order }) }
        _events.send(VideosSectionUiEvent.ShowMessage(UiText.Resource(R.string.videos_added)))
    }

    fun onCancelUploadClick() {
        if (uploadJob?.isActive != true) return

        uploadJob?.cancel()
        uploadJob = null
        addVideoForm.update {
            it.copy(
                isUploading = false,
                progressPercent = null,
                errorMessage = UiText.Resource(R.string.videos_upload_cancelled),
            )
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
