package com.strimup.feature.streamerdetail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strimup.feature.streamerdetail.domain.usecase.GetStreamerUsecase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class StreamerDetailViewModel @Inject constructor(
    private val getStreamer: GetStreamerUsecase,
) : ViewModel() {

    private val _state = MutableStateFlow<StreamerDetailUiState>(StreamerDetailUiState.Loading)
    val state: StateFlow<StreamerDetailUiState> = _state.asStateFlow()

//    init {
//        loadStreamer(streamerId)
//    }

    fun loadStreamer(id: String) {
        viewModelScope.launch {
            _state.value = StreamerDetailUiState.Loading

            getStreamer(id)
                .onSuccess { streamer ->
                    _state.value = StreamerDetailUiState.Success(streamer = streamer)
                }
                .onFailure { exception ->
                    _state.value = StreamerDetailUiState.Error(
                        message = exception.localizedMessage ?: "Impossible de charger le streamer"
                    )
                }
        }
    }

//    @AssistedFactory
//    interface Factory {
//        fun create(streamerId: String): StreamerDetailViewModel
//    }
}