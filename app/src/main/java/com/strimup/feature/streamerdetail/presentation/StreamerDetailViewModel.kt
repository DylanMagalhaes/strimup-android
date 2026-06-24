package com.strimup.feature.streamerdetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.streamerdetail.domain.usecase.GetStreamerUsecase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = StreamerDetailViewModel.Factory::class)
class StreamerDetailViewModel @AssistedInject constructor(
    @Assisted val streamerId: String,
    private val getStreamer: GetStreamerUsecase,
) : ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    init {
        loadStreamer(streamerId)
    }

    private fun loadStreamer(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true) }
            getStreamer(id)
                .onSuccess { streamer ->
                    _state.update { it.copy(loading = false, streamer = streamer) }
                }
        }
    }

    @AssistedFactory interface Factory {
        fun create(streamerId: String): StreamerDetailViewModel
    }
}
