package com.strimup.feature.schedule.presentation.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.R
import com.strimup.core.network.toDomainError
import com.strimup.core.ui.error.toUiText
import com.strimup.core.ui.text.UiText
import com.strimup.feature.schedule.domain.usecase.GetMyScheduleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleExportViewModel @Inject constructor(
    private val getMySchedule: GetMyScheduleUseCase,
    private val imageGenerator: ScheduleExportImageGenerator,
    private val gallerySaver: ScheduleImageGallerySaver,
) : ViewModel() {

    private val _state = MutableStateFlow(ScheduleExportUiState(isGallerySaveAvailable = gallerySaver.isAvailable))
    val state: StateFlow<ScheduleExportUiState> = _state.asStateFlow()

    private val _events = Channel<ScheduleExportUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var username: String = ""
    private var days: List<ScheduleExportDay>? = null
    private var job: Job? = null

    fun load(username: String) {
        if (username == this.username && days != null) return

        this.username = username
        job?.cancel()
        _state.update { it.copy(isGenerating = true, errorMessage = null) }

        job = viewModelScope.launch {
            getMySchedule()
                .onSuccess { items ->
                    days = items.toExportDays()
                    generate()
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(isGenerating = false, errorMessage = exception.toDomainError().toUiText())
                    }
                }
        }
    }

    fun onTemplateSelected(template: ScheduleExportTemplate) {
        if (template == _state.value.selectedTemplate) return

        _state.update { it.copy(selectedTemplate = template) }
        if (days == null) return

        job?.cancel()
        job = viewModelScope.launch { generate() }
    }

    fun onRetryClick() {
        if (days == null) {
            load(username)
        } else {
            job?.cancel()
            job = viewModelScope.launch { generate() }
        }
    }

    fun onSaveToGalleryClick() {
        val currentState = _state.value
        val file = currentState.imageFile
        if (!currentState.isImageReady || currentState.isSavingToGallery || file == null) return

        _state.update { it.copy(isSavingToGallery = true) }

        viewModelScope.launch {
            val messageRes = gallerySaver.save(file).fold(
                onSuccess = { R.string.schedule_export_saved },
                onFailure = { R.string.schedule_export_save_error },
            )
            _state.update { it.copy(isSavingToGallery = false) }
            _events.send(ScheduleExportUiEvent.ShowSnackBar(messageRes))
        }
    }

    private suspend fun generate() {
        val exportDays = days ?: return
        _state.update { it.copy(isGenerating = true, errorMessage = null) }

        imageGenerator.generate(username = username, days = exportDays, template = _state.value.selectedTemplate)
            .onSuccess { file ->
                _state.update { it.copy(imageFile = file, isGenerating = false) }
            }
            .onFailure {
                _state.update {
                    it.copy(isGenerating = false, errorMessage = UiText.Resource(R.string.schedule_export_error))
                }
            }
    }
}
